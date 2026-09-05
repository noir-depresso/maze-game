package fgp.engine.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

/**
 * @author Mr. Hapke
 *
 */
public class ImageLoader {
	private Class<?> cls;
	private boolean jarMode;
	private boolean fsMode;

	public ImageLoader(Class<?> cls) {
		this.cls = cls;
		URL resource = cls.getResource(cls.getSimpleName() + ".class");
		String protocol = resource.getProtocol();
		if (Objects.equals(protocol, "jar")) {
			jarMode = true;
		} else if (Objects.equals(protocol, "file")) {
			fsMode = true;
		}
	}

	public Image getImage(String folder, String filename) {
		try {

			BufferedImage img = null;
			if (jarMode) {
				InputStream in = searchJar(cls, folder, filename);
				img = ImageIO.read(in);
			} else if (fsMode) {
				ProtectionDomain pd = cls.getProtectionDomain();
				File f = getFileNotInBinFolder(pd, folder + File.separatorChar + filename);
				img = ImageIO.read(f);
			}
			return img;
		} catch (IOException e) {
			return null;
		}
	}

	public static File getFileNotInBinFolder(ProtectionDomain pd, String fn) throws IOException {
		URL url = pd.getCodeSource().getLocation();

		File f;
		File dir = new File(url.getFile());
		int i = 0;
		while (i < 100) {
			String pathname = dir.getAbsolutePath() + File.separatorChar + fn;

			// FYI: Manages spaces in file/foldernames
			Pattern pattern = Pattern.compile(" ");
			Matcher matcher = pattern.matcher(pathname);
			if (matcher.matches()) {
				pathname = matcher.replaceAll("\\ ");
			}
			f = new File(pathname);
			if (f.exists())
				return f;
			dir = dir.getParentFile();
			if (dir == null || !dir.canRead())
				return null;
			i++;
		}
		System.err.println("File not found");
		return null;
	}

	public static InputStream searchJar(Class<?> cls, String folder, String filename) {
		String path;
		if (folder != null && folder.length() > 0) {
			path = '/' + folder + '/' + filename;
		} else {
			path = '/' + filename;
		}
		InputStream in = cls.getResourceAsStream(path);
		return in;
	}

}
