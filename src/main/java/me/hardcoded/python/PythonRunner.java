package me.hardcoded.python;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonRunner {
	static final Path dataPath;
	static final File venvFile;
	
	static {
		String tmp = System.getProperty("java.io.tmpdir");
		dataPath = Path.of(tmp, "music-library-data");
		venvFile = Path.of(tmp, "music-library-data", "venv").toFile();
		
		// TODO: Make sure the path is writable / configurable
		
		// Create path
		venvFile.mkdirs();
		
		System.out.println("Data: " + dataPath);
	}
	
	public static void init() {
		// If it's not initialized
		if (venvFile.length() == 0) {
			System.out.println(venvFile);
			try {
				int code = new ProcessBuilder("python", "-m", "venv", venvFile.toString())
					.start()
					.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		// Download packages
		installPackage("pyflp");
		
		// Testing
		// 24 per step
	}
	
	private static int installPackage(String name) {
		String activate = Path.of(venvFile.toString(), "Scripts", "activate.bat").toString();
		
		try {
			// Install python requirements
			return new ProcessBuilder("cmd.exe", "/c", activate + " & py -m pip install " + name)
				.redirectErrorStream(true)
				.inheritIO()
				.start()
				.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		return -1;
	}
	
	public static String run(String code) {
		File file = null;
		try {
			file = Files.createTempFile("python-lib", ".py").toFile();
			try (FileOutputStream fs = new FileOutputStream(file)) {
				fs.write(code.getBytes(StandardCharsets.UTF_8));
			}
			
			String activate = Path.of(venvFile.toString(), "Scripts", "activate.bat").toString();
			
			try {
				// Install python requirements
				Process process = new ProcessBuilder("cmd.exe", "/c", activate + " & py \"" + file + "\"")
					.redirectErrorStream(true)
					.start();
				
				byte[] bytes = process.getInputStream().readAllBytes();
				int exitCode = process.waitFor();
				
				return new String(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			// Create a venv 
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (file != null) {
				file.delete();
			}
		}
		
		return null;
	}
}
