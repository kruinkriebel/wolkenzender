package nl.ser1.zender.scooped.moviemaker;

/**
 * Adapted version:
 * - Added NPE fix
 * - SLF4J logging
 * - Removed main / command line methods
 * <p>
 * Original Javadoc:
 *
 * @(#)JpegImagesToMovie.java 1.3 01/03/13
 * <p>
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 * <p>
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * <p>
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * <p>
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Vector;


/**
 * This program takes a list of JPEG image files and convert them into
 * a QuickTime movie.
 */
public class JpegImagesToMovie implements ControllerListener, DataSinkListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpegImagesToMovie.class);

    Object waitSync = new Object();
    boolean stateTransitionOK = true;
    Object waitFileSync = new Object();
    boolean fileDone = false;
    boolean fileSuccess = true;

    public static MediaLocator createMediaLocator(String url) throws MalformedURLException {
        return new MediaLocator(new URL("file:" + url));

    }

    public boolean doIt(int width, int height, int frameRate, Vector inFiles, MediaLocator outML) {
        ImageDataSource ids = new ImageDataSource(width, height, frameRate, inFiles);

        Processor p;

        try {
            p = Manager.createProcessor(ids);
        } catch (Exception e) {
            return false;
        }

        p.addControllerListener(this);

        // Put the Processor into configured state so we can set
        // some processing options on the processor.
        p.configure();
        if (!waitForState(p, Processor.Configured)) {
            LOGGER.error("Failed to configure the processor.");
            return false;
        }

        // Set the output content descriptor to QuickTime.
        p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));

        // Query for the processor for supported formats.
        // Then set it on the processor.
        TrackControl[] trackControls = p.getTrackControls();

        Arrays.asList(trackControls).forEach(tc -> LOGGER.info("Track control: " + tc));

        Format[] supportedFormats = trackControls[0].getSupportedFormats();
        if (supportedFormats == null || supportedFormats.length <= 0) {
            LOGGER.error("The mux does not support the input format: " + trackControls[0].getFormat());
            return false;
        }

        Arrays.asList(supportedFormats).forEach(sf -> LOGGER.info("Supported format: " + sf.getEncoding()));

        trackControls[0].setFormat(supportedFormats[0]);

        // We are done with programming the processor.  Let's just
        // realize it.
        p.realize();
        if (!waitForState(p, Processor.Realized)) {
            LOGGER.error("Failed to realize the processor.");
            return false;
        }

        // Now, we'll need to create a DataSink.
        DataSink dsink;
        if ((dsink = createDataSink(p, outML)) == null) {
            LOGGER.error("Failed to create a DataSink for the given output MediaLocator: " + outML);
            return false;
        }

        dsink.addDataSinkListener(this);
        fileDone = false;

        // OK, we can now start the actual transcoding.
        try {
            p.start();
            dsink.start();
        } catch (IOException e) {
            LOGGER.error("IO error during processing", e);
            return false;
        }

        // Wait for EndOfStream event.
        waitForFileDone();

        // Cleanup.
        try {
            dsink.close();
        } catch (Exception e) {
            LOGGER.error("While closing datasink", e);
        }

        p.removeControllerListener(this);

        return true;
    }

    /**
     * Create the DataSink.
     */
    DataSink createDataSink(Processor p, MediaLocator outML) {
        DataSource ds;

        if ((ds = p.getDataOutput()) == null) {
            LOGGER.error("Something is really wrong: the processor does not have an output DataSource (null)");
            return null;
        }

        DataSink dsink;

        try {
            dsink = Manager.createDataSink(ds, outML);
            dsink.open();
        } catch (Exception e) {
            LOGGER.error("Cannot create the DataSink", e);
            return null;
        }

        return dsink;
    }

    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    boolean waitForState(Processor p, int state) {
        synchronized (waitSync) {
            try {
                while (p.getState() < state && stateTransitionOK)
                    waitSync.wait();
            } catch (Exception e) {

            }
        }
        return stateTransitionOK;
    }

    /**
     * Controller Listener.
     */
    public void controllerUpdate(ControllerEvent evt) {
        if (evt instanceof ConfigureCompleteEvent ||
                evt instanceof RealizeCompleteEvent ||
                evt instanceof PrefetchCompleteEvent) {
            synchronized (waitSync) {
                stateTransitionOK = true;
                waitSync.notifyAll();
            }
        } else if (evt instanceof ResourceUnavailableEvent) {
            synchronized (waitSync) {
                stateTransitionOK = false;
                waitSync.notifyAll();
            }
        } else if (evt instanceof EndOfMediaEvent) {
            if (evt.getSourceController() != null) {
                evt.getSourceController().stop();
                evt.getSourceController().close();
            }
        }
    }

    /**
     * Block until file writing is done.
     */
    boolean waitForFileDone() {
        synchronized (waitFileSync) {
            try {
                while (!fileDone)
                    waitFileSync.wait();
            } catch (Exception e) {

            }
        }
        return fileSuccess;
    }

    /**
     * Event handler for the file writer.
     */
    public void dataSinkUpdate(DataSinkEvent evt) {

        if (evt instanceof EndOfStreamEvent) {
            synchronized (waitFileSync) {
                fileDone = true;
                waitFileSync.notifyAll();
            }
        } else if (evt instanceof DataSinkErrorEvent) {
            synchronized (waitFileSync) {
                fileDone = true;
                fileSuccess = false;
                waitFileSync.notifyAll();
            }
        }
    }

    /**
     * A DataSource to read from a list of JPEG image files and
     * turn that into a stream of JMF buffers.
     * The DataSource is not seekable or positionable.
     */
    class ImageDataSource extends PullBufferDataSource {

        ImageSourceStream streams[];

        ImageDataSource(int width, int height, int frameRate, Vector images) {
            streams = new ImageSourceStream[1];
            streams[0] = new PngImageSourceStream(width, height, frameRate, images);
        }

        public MediaLocator getLocator() {
            return null;
        }

        public void setLocator(MediaLocator source) {
        }

        /**
         * Content type is of RAW since we are sending buffers of video
         * frames without a container format.
         */
        public String getContentType() {
            return ContentDescriptor.RAW;
        }

        public void connect() {
        }

        public void disconnect() {
        }

        public void start() {
        }

        public void stop() {
        }

        /**
         * Return the ImageSourceStreams.
         */
        public PullBufferStream[] getStreams() {
            return streams;
        }

        /**
         * We could have derived the duration from the number of
         * frames and frame rate.  But for the purpose of this program,
         * it's not necessary.
         */
        public Time getDuration() {
            return DURATION_UNKNOWN;
        }

        public Object[] getControls() {
            return new Object[0];
        }

        public Object getControl(String type) {
            return null;
        }
    }


    /**
     * The source stream to go along with ImageDataSource.
     */
    class ImageSourceStream implements PullBufferStream {
        Vector images;
        int width, height;
        VideoFormat format;

        int nextImage = 0;    // index of the next image to be read.
        boolean ended = false;

        public ImageSourceStream(int width, int height, int frameRate, Vector images) {
            this.width = width;
            this.height = height;
            this.images = images;

            format = new VideoFormat(VideoFormat.JPEG,
                    new Dimension(width, height),
                    Format.NOT_SPECIFIED,
                    Format.byteArray,
                    (float) frameRate);
        }

        /**
         * We should never need to block assuming data are read from files.
         */
        public boolean willReadBlock() {
            return false;
        }

        /**
         * This is called from the Processor to read a frame worth
         * of video data.
         */
        public void read(Buffer buf) throws IOException {

            // Check if we've finished all the frames.
            if (nextImage >= images.size()) {
                // We are done.  Set EndOfMedia.
                buf.setEOM(true);
                buf.setOffset(0);
                buf.setLength(0);
                ended = true;
                return;
            }

            String imageFile = (String) images.elementAt(nextImage);
            nextImage++;

            // Open a random access file for the next image.
            RandomAccessFile raFile;
            raFile = new RandomAccessFile(imageFile, "r");

            byte data[] = null;

            // Check the input buffer type & size.

            if (buf.getData() instanceof byte[])
                data = (byte[]) buf.getData();

            // Check to see the given buffer is big enough for the frame.
            if (data == null || data.length < raFile.length()) {
                data = new byte[(int) raFile.length()];
                buf.setData(data);
            }

            // Read the entire JPEG image from the file.
            raFile.readFully(data, 0, (int) raFile.length());

            buf.setOffset(0);
            buf.setLength((int) raFile.length());
            buf.setFormat(format);
            buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);

            // Close the random access file.
            raFile.close();
        }

        /**
         * Return the format of each video frame.  That will be JPEG.
         */
        public Format getFormat() {
            return format;
        }

        public ContentDescriptor getContentDescriptor() {
            return new ContentDescriptor(ContentDescriptor.RAW);
        }

        public long getContentLength() {
            return 0;
        }

        public boolean endOfStream() {
            return ended;
        }

        public Object[] getControls() {
            return new Object[0];
        }

        public Object getControl(String type) {
            return null;
        }
    }

    class PngImageSourceStream extends ImageSourceStream {

        public PngImageSourceStream(int width, int height, int frameRate,
                                    Vector<String> images) {
            super(width, height, frameRate, images);

            // configure the new format as RGB format
            format = new RGBFormat(new Dimension(width, height),
                    Format.NOT_SPECIFIED, Format.byteArray, frameRate,
                    24, // 24 bits per pixel
                    1, 2, 3); // red, green and blue masks when data are in the form of byte[]
        }

        public void read(Buffer buf) throws IOException {

            // Check if we've finished all the frames.
            if (nextImage >= images.size()) {
                // We are done. Set EndOfMedia.
                LOGGER.info("Done reading all images.");
                buf.setEOM(true);
                buf.setOffset(0);
                buf.setLength(0);
                ended = true;
                return;
            }

            String imageFile = (String) images.elementAt(nextImage);
            nextImage++;

            LOGGER.error("  - reading image file: " + imageFile);

            // read the PNG image
            BufferedImage image = ImageIO.read(new File(imageFile));
            boolean hasAlpha = image.getColorModel().hasAlpha();
            Dimension size = format.getSize();

            // convert 32-bit RGBA to 24-bit RGB
            byte[] imageData = convertTo24Bit(hasAlpha, image.getRaster().getPixels(0, 0, size.width, size.height, (int[]) null));
            buf.setData(imageData);

            LOGGER.error("    read " + imageData.length + " bytes.");

            buf.setOffset(0);
            buf.setLength(imageData.length);
            buf.setFormat(format);
            buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
        }

        private void convertIntByteToByte(int[] src, int srcIndex, byte[] out, int outIndex) {
            // Note: the int[] returned by bufferedImage.getRaster().getPixels()
            // is an int[]
            // where each int is the value for one color i.e. the first 4 ints
            // contain the RGBA values for the first pixel
            int r = src[srcIndex];
            int g = src[srcIndex + 1];
            int b = src[srcIndex + 2];

            out[outIndex] = (byte) (r & 0xFF);
            out[outIndex + 1] = (byte) (g & 0xFF);
            out[outIndex + 2] = (byte) (b & 0xFF);
        }

        private byte[] convertTo24Bit(boolean hasAlpha, int[] input) {
            int dataLength = input.length;
            int newSize = (hasAlpha ? dataLength * 3 / 4 : dataLength);
            byte[] convertedData = new byte[newSize];

            // for every 4 int values of the original array (RGBA) write 3
            // bytes (RGB) to the output array
            // if there is no alpha (i.e. RGB image) then just convert int to byte
            for (int i = 0, j = 0; i < dataLength; i += 3, j += 3) {
                convertIntByteToByte(input, i, convertedData, j);
                if (hasAlpha) {
                    i++; // skip an extra byte if the original image has an
                    // extra int for transparency
                }
            }
            return convertedData;
        }

    }
}