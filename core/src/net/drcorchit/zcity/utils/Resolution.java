package net.drcorchit.zcity.utils;

public enum Resolution {
	SD(640, 480), HD_720(1280, 720), HD_WXGA(1366, 768), HD_1080(1920, 1080), HD_4K(3840, 2160);

	public final int width, height;

	Resolution(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
