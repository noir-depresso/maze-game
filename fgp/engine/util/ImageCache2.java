package fgp.engine.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * @author Mr. Hapke
 */
public class ImageCache2 {
	private static ImageCache2 instance = new ImageCache2();
	public static final boolean PICTURE_DEBUG = false;

	public static ImageCache2 getInstance() {
		return instance;
	}

	private ImageCache2() {
	}

	private static final String DELIMITER = "$";

	private Map<String, Image> cache = new HashMap<>();
	private Map<Image, String> reverseCache = new HashMap<>();
	private Map<String, Image> scaledCache = new HashMap<>();

	public Image getImage(String folder, String filename) {
		String key = getFilenameKey(folder, filename);
		Image image = cache.get(key);
		if (image == null) {
			Class<? extends ImageCache2> cls = this.getClass();
			URL resource = cls.getResource(cls.getSimpleName() + ".class");
			String protocol = resource.getProtocol();

			try {
				if (Objects.equals(protocol, "jar")) {
					InputStream in = searchJar(folder, filename);
					image = ImageIO.read(in);

				} else if (Objects.equals(protocol, "file")) {
					File f;
					f = searchFilesystem(folder, filename);
					if (f == null) {
						f = searchViaClassloader(folder, filename);
					}
					image = ImageIO.read(f);
				}
				if (PICTURE_DEBUG && image != null) {
					System.out.println("Found it!");
				}
				cache.put(key, image);
				reverseCache.put(image, key);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return image;
	}

	public InputStream searchJar(String folder, String filename) {
		String path;
		if (folder != null && folder.length() > 0) {
			path = '/' + folder + '/' + filename;
		} else {
			path = '/' + filename;
		}
		InputStream in = getClass().getResourceAsStream(path);
		return in;
	}

	public File searchViaClassloader(String folder, String filename) {
		ProtectionDomain pd = getClass().getProtectionDomain();
		ClassLoader cl = pd.getClassLoader();

		File f;
		int i = 0;
		String fullFilename = (folder != null ? (folder + File.separatorChar) : "") + filename;
		while (i < 10) {
			URL res = cl.getResource(fullFilename);
			try {
				URI uri = res.toURI();

				f = new File(uri);
				if (f.exists())
					return f;
			} catch (Exception e) {
				System.err.println("[CL]: URI Exception");
			}
			fullFilename = ".." + File.separatorChar + fullFilename;
			i++;
		}
		System.err.println("File not found [CL]: Folder[" + folder + "] Filename[" + filename + "]");
		return null;
	}

	public File searchFilesystem(String folder, String filename) {
		String userDir = System.getProperty("user.dir");
		File f;
		File dir = new File(userDir);
		int i = 0;
		while (i < 10) {
			String slashFolder = folder != null ? (File.separatorChar + folder) : "";
			String path = dir.getAbsolutePath() + slashFolder + File.separatorChar + filename;
			f = new File(path);
			if (f.exists())
				return f;
			dir = dir.getParentFile();
			if (dir == null || !dir.canRead())
				break;
			i++;
		}
		System.err.println("File not found [FS]: Folder[" + folder + "] Filename[" + filename + "]");
		return null;
	}

	private static String getFilenameKey(String folder, String filename) {
		return folder + DELIMITER + filename;
	}

	private static String getScaledKey(String filenameKey, int boxWidth, int boxHeight) {
		return filenameKey + DELIMITER + boxWidth + DELIMITER + boxHeight;
	}

	public Image getScaled(Image original, int width, int height) {
		if (original == null || width == 0 || height == 0)
			return null;

		String originalKey = reverseCache.get(original);
		String scaledKey = getScaledKey(originalKey, width, height);

		Image scaled = scaledCache.get(scaledKey);
		if (scaled == null) {
			double boxAspect = ((double) width) / height;
			int originalW = original.getWidth(null);
			int originalH = original.getHeight(null);
			double originalAspect = ((double) originalW) / originalH;
			int w, h;
			if (originalAspect > boxAspect) {
				// image wider, so scale height
				w = width;
				h = (int) (width / originalAspect);
			} else {
				w = (int) (height * originalAspect);
				h = height;
			}
			scaled = original.getScaledInstance(w, h, Image.SCALE_SMOOTH);
			scaledCache.put(scaledKey, scaled);
		}
		return scaled;
	}
}
