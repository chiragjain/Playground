package com.learn.playground.box2dview;

import java.util.Random;

public class Ball {

		public static final int COLOR_YELLOW = 0;
		public static final int COLOR_VIOLET = 1;
		public static final int COLOR_GRAY = 2;
		public static final int COLOR_BLUE = 3;
		public static final int COLOR_GREEN = 4;
		public static final int COLOR_RED = 5;

		private String color;
		private String number;

		public Ball(int color, String number) {
			this.color = getBallColor(color);
			this.number = number;
		}

		public Ball(String number) {
			this.color = getRandomBallColor();
			this.number = number;
		}

		public void setColor(int color) {
			this.color = getBallColor(color);
		}

		public String getColorCode() {
			return this.color;
		}

		public String getNumber() {
			String tempNum = "";
			if (number.length() > 3)
				tempNum = number.substring(0, 3);
			else
				tempNum = number;

			return tempNum;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public static String getRandomBallColor() {
			Random r = new Random();
			// It will not include Red
			return getBallColor(r.nextInt(COLOR_RED));
		}

		public static String getBallColor(int colorCode) {
			switch (colorCode) {
			case COLOR_YELLOW:
				return "ffff00";
			case COLOR_VIOLET:
				return "ff00b2";
			case COLOR_GRAY:
				return "b9b9b9";
			case COLOR_BLUE:
				return "00faff";
			case COLOR_GREEN:
				return "00ff3e";
			case COLOR_RED:
				return "ff0000";
			default:
				return "ffbd00";
			}
		}
	}