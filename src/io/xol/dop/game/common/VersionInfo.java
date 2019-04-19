package io.xol.dop.game.common;

//(c) 2014 XolioWare Interactive

public class VersionInfo {

	public static String version = "0.3.3";
	public static String devPhase = "indev";
	public static String branch = "units-indev";

	public static String get() {
		return version + "-" + devPhase + "/" + branch;
	}
}
