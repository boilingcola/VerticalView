/*****************************************************************************
 * Logcat.java
 *****************************************************************************
 * Copyright 漏 2011-2014 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.example.test_marquee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.text.format.DateFormat;
import android.util.Log;

public class Logcat {
	public final static String TAG = "VLC/Util/Logcat";

	/**
	 * Writes the current app logcat to a file.
	 * 
	 * @param filename
	 *            The filename to save it as
	 * @throws IOException
	 */
	public static void writeLogcat(String filename) throws IOException {
		String[] args = { "logcat", "-v", "time", "-d" };

		Process process = Runtime.getRuntime().exec(args);

		InputStreamReader input = new InputStreamReader(
				process.getInputStream());

		FileOutputStream fileStream;
		try {
			fileStream = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			return;
		}

		OutputStreamWriter output = new OutputStreamWriter(fileStream);
		BufferedReader br = new BufferedReader(input);
		BufferedWriter bw = new BufferedWriter(output);

		try {
			String line;
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.newLine();
			}
		} catch (Exception e) {
		} finally {
			bw.close();
			output.close();
			br.close();
			input.close();
		}
	}

	/**
	 * Get the last 500 lines of the application logcat.
	 * 
	 * @return the log string.
	 * @throws IOException
	 */
	public static String getLogcat() throws IOException {
		String[] args = { "logcat", "-v", "time", "-d", "-t", "500" };

		Process process = Runtime.getRuntime().exec(args);
		InputStreamReader input = new InputStreamReader(
				process.getInputStream());
		BufferedReader br = new BufferedReader(input);
		StringBuilder log = new StringBuilder();
		String line;

		while ((line = br.readLine()) != null)
			log.append(line + "\n");

		br.close();
		input.close();

		return log.toString();
	}

	/**
	 * 将信息保存到日志中 文件在/sdcard/videoPlayer/Log/Msg.txt中
	 * 
	 * @param msg
	 *            要保存的信息
	 */
	public static void saveMsgToDisk(final String msg) {
		new Thread() {
			@Override
			public void run() {
				super.run();

				try {

					CharSequence timestamp = DateFormat.format(
							"yyyy-MM-dd_kk:mm:ss", System.currentTimeMillis());
					String str = timestamp + "    " + msg + "\n";
					FileOutputStream fos = new FileOutputStream(
							"", true);
					fos.write(str.getBytes());
					fos.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();

	}

	public static void d(String TAG, String content) {
		Log.d(TAG, "--- " + content + " ---");
	}

	public static void e(String TAG, String content) {
		Log.e(TAG, "--- " + content + " ---");
	}

	public static void v(String TAG, String content) {
		Log.v(TAG, "--- " + content + " ---");
	}

	public static void exec_cmd(final String cmd) {
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					Process p;
					p = Runtime.getRuntime().exec("su");
					DataOutputStream os = new DataOutputStream(
							p.getOutputStream());
					os.writeBytes(cmd + "\n");
					os.writeBytes("exit\n");
					os.flush();
					Log.e("exec_cmd: ", cmd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}
}
