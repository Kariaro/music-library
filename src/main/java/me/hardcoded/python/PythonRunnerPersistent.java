package me.hardcoded.python;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class PythonRunnerPersistent {
	private Process process;
	
	/**
	 * Start executing the file
	 * @param file the file to execute
	 * @param arguments arguments   
	 */
	public void start(File file, List<String> arguments) {
		String activate = Path.of(PythonRunner.venvFile.toString(), "Scripts", "activate.bat").toString();
		
		try {
			// Install python requirements
			process = new ProcessBuilder("cmd.exe", "/c", activate + " & py \"" + file + "\" " + String.join(" ", arguments))
				.redirectErrorStream(true)
				.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send data to the process and expect a return value
	 * 
	 * @param sendData the data sent to the process
	 */
	public String sendInput(byte[] sendData) {
		Process localProcess = process;
		if (localProcess == null) {
			// Throw exception for invalid state?
			return null;
		}
		
		try {
			OutputStream outputStream = localProcess.getOutputStream();
			outputStream.write(sendData);
			outputStream.write('\n');
			outputStream.flush(); // New line is needed to send command + flush
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
			
			// TODO: Max timeout?
			while (!reader.ready()) {
				Thread.yield();
			}
			
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void stop() {
		Process localProcess = process;
		if (localProcess != null) {
			localProcess.destroyForcibly();
		}
	}
}
