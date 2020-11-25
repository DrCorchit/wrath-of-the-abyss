package net.drcorchit.zcity.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Scripts {

	public static void main(String... args) {

		JsonArray frames = new JsonArray();

		for (int i = 0; i < 60; i++) {
			float p = (float) Math.sin(Math.toRadians(i * 6));

			JsonObject output = new JsonObject();
			output.addProperty("left_shoulder", 15 * p);
			output.addProperty("right_shoulder", -15 * p);
			output.addProperty("left_elbow", 10 + 10 * p);
			output.addProperty("right_elbow", 10 - 10 * p);

			float knee_mult, hip_offset;
			hip_offset = 10;
			knee_mult = 3;

			output.addProperty("root", -3 - 3 * Math.abs(p));

			float offset_x_targ = 0;
			float offset_y_targ = (float) (3 * Math.abs(p));
			float offset_a_targ = 0;

			float lh = 0, rh = 0, lk = 0, rk = 0;

			switch (i) {
				case 0:
					lh = hip_offset + 0;
					rh = hip_offset + 0;
					lk = 0;
					rk = knee_mult * -20;
					break;
				case 1:
					lh = hip_offset - 2;
					rh = hip_offset + 2;
					lk = 0;
					rk = knee_mult * -19;
					break;
				case 2:
					lh = hip_offset - 4;
					rh = hip_offset + 4;
					lk = 0;
					rk = knee_mult * -18;
					break;
				case 3:
					lh = hip_offset - 6;
					rh = hip_offset + 6;
					lk = 0;
					rk = knee_mult * -17;
					break;
				case 4:
					lh = hip_offset - 8;
					rh = hip_offset + 8;
					lk = 0;
					rk = knee_mult * -16;
					break;
				case 5:
					lh = hip_offset - 10;
					rh = hip_offset + 10;
					lk = 0;
					rk = knee_mult * -15;
					break;
				case 6:
					lh = hip_offset - 12;
					rh = hip_offset + 12;
					lk = 0;
					rk = knee_mult * -14;
					break;
				case 7:
					lh = hip_offset - 14;
					rh = hip_offset + 14;
					lk = 0;
					rk = knee_mult * -13;
					break;
				case 8:
					lh = hip_offset - 16;
					rh = hip_offset + 16;
					lk = 0;
					rk = knee_mult * -12;
					break;
				case 9:
					lh = hip_offset - 17;
					rh = hip_offset + 17;
					lk = 0;
					rk = knee_mult * -11;
					break;
				case 10:
					lh = hip_offset - 19;
					rh = hip_offset + 19;
					lk = 0;
					rk = knee_mult * -10;
					break;
				case 11:
					lh = hip_offset - 20;
					rh = hip_offset + 20;
					lk = knee_mult * -1;
					rk = knee_mult * -9;
					break;
				case 12:
					lh = hip_offset - 22;
					rh = hip_offset + 22;
					lk = knee_mult * -2;
					rk = knee_mult * -8;
					break;
				case 13:
					lh = hip_offset - 23;
					rh = hip_offset + 23;
					lk = knee_mult * -3;
					rk = knee_mult * -7;
					break;
				case 14:
					lh = hip_offset - 24;
					rh = hip_offset + 24;
					lk = knee_mult * -4;
					rk = knee_mult * -6;
					break;
				case 15:
					lh = hip_offset - 25;
					rh = hip_offset + 25;
					lk = knee_mult * -5;
					rk = knee_mult * -5;
					break;
				case 16:
					lh = hip_offset - 24;
					rh = hip_offset + 24;
					lk = knee_mult * -6;
					rk = knee_mult * -4;
					break;
				case 17:
					lh = hip_offset - 23;
					rh = hip_offset + 23;
					lk = knee_mult * -7;
					rk = knee_mult * -3;
					break;
				case 18:
					lh = hip_offset - 22;
					rh = hip_offset + 22;
					lk = knee_mult * -8;
					rk = knee_mult * -2;
					break;
				case 19:
					lh = hip_offset - 20;
					rh = hip_offset + 20;
					lk = knee_mult * -9;
					rk = knee_mult * -1;
					break;
				case 20:
					lh = hip_offset - 19;
					rh = hip_offset + 19;
					lk = knee_mult * -10;
					rk = 0;
					break;
				case 21:
					lh = hip_offset - 17;
					rh = hip_offset + 17;
					lk = knee_mult * -11;
					rk = 0;
					break;
				case 22:
					lh = hip_offset - 16;
					rh = hip_offset + 16;
					lk = knee_mult * -12;
					rk = 0;
					break;
				case 23:
					lh = hip_offset - 14;
					rh = hip_offset + 14;
					lk = knee_mult * -13;
					rk = 0;
					break;
				case 24:
					lh = hip_offset - 12;
					rh = hip_offset + 12;
					lk = knee_mult * -14;
					rk = 0;
					break;
				case 25:
					lh = hip_offset - 10;
					rh = hip_offset + 10;
					lk = knee_mult * -15;
					rk = 0;
					break;
				case 26:
					lh = hip_offset - 8;
					rh = hip_offset + 8;
					lk = knee_mult * -16;
					rk = 0;
					break;
				case 27:
					lh = hip_offset - 6;
					rh = hip_offset + 6;
					lk = knee_mult * -17;
					rk = 0;
					break;
				case 28:
					lh = hip_offset - 4;
					rh = hip_offset + 4;
					lk = knee_mult * -18;
					rk = 0;
					break;
				case 29:
					lh = hip_offset - 2;
					rh = hip_offset + 2;
					lk = knee_mult * -19;
					rk = 0;
					break;
				case 30:
					lh = hip_offset + 0;
					rh = hip_offset + 0;
					lk = knee_mult * -20;
					rk = 0;
					break;
				case 31:
					lh = hip_offset + 2;
					rh = hip_offset - 2;
					lk = knee_mult * -19;
					rk = 0;
					break;
				case 32:
					lh = hip_offset + 4;
					rh = hip_offset - 4;
					lk = knee_mult * -18;
					rk = 0;
					break;
				case 33:
					lh = hip_offset + 6;
					rh = hip_offset - 6;
					lk = knee_mult * -17;
					rk = 0;
					break;
				case 34:
					lh = hip_offset + 8;
					rh = hip_offset - 8;
					lk = knee_mult * -16;
					rk = 0;
					break;
				case 35:
					lh = hip_offset + 10;
					rh = hip_offset - 10;
					lk = knee_mult * -15;
					rk = 0;
					break;
				case 36:
					lh = hip_offset + 12;
					rh = hip_offset - 12;
					lk = knee_mult * -14;
					rk = 0;
					break;
				case 37:
					lh = hip_offset + 14;
					rh = hip_offset - 14;
					lk = knee_mult * -13;
					rk = 0;
					break;
				case 38:
					lh = hip_offset + 16;
					rh = hip_offset - 16;
					lk = knee_mult * -12;
					rk = 0;
					break;
				case 39:
					lh = hip_offset + 17;
					rh = hip_offset - 17;
					lk = knee_mult * -11;
					rk = 0;
					break;
				case 40:
					lh = hip_offset + 19;
					rh = hip_offset - 19;
					lk = knee_mult * -10;
					rk = 0;
					break;
				case 41:
					lh = hip_offset + 20;
					rh = hip_offset - 20;
					lk = knee_mult * -9;
					rk = knee_mult * -1;
					break;
				case 42:
					lh = hip_offset + 22;
					rh = hip_offset - 22;
					lk = knee_mult * -8;
					rk = knee_mult * -2;
					break;
				case 43:
					lh = hip_offset + 23;
					rh = hip_offset - 23;
					lk = knee_mult * -7;
					rk = knee_mult * -3;
					break;
				case 44:
					lh = hip_offset + 24;
					rh = hip_offset - 24;
					lk = knee_mult * -6;
					rk = knee_mult * -4;
					break;
				case 45:
					lh = hip_offset + 25;
					rh = hip_offset - 25;
					lk = knee_mult * -5;
					rk = knee_mult * -5;
					break;
				case 46:
					lh = hip_offset + 24;
					rh = hip_offset - 24;
					lk = knee_mult * -4;
					rk = knee_mult * -6;
					break;
				case 47:
					lh = hip_offset + 23;
					rh = hip_offset - 23;
					lk = knee_mult * -3;
					rk = knee_mult * -7;
					break;
				case 48:
					lh = hip_offset + 22;
					rh = hip_offset - 22;
					lk = knee_mult * -2;
					rk = knee_mult * -8;
					break;
				case 49:
					lh = hip_offset + 20;
					rh = hip_offset - 20;
					lk = knee_mult * -1;
					rk = knee_mult * -9;
					break;
				case 50:
					lh = hip_offset + 19;
					rh = hip_offset - 19;
					lk = 0;
					rk = knee_mult * -10;
					break;
				case 51:
					lh = hip_offset + 17;
					rh = hip_offset - 17;
					lk = 0;
					rk = knee_mult * -11;
					break;
				case 52:
					lh = hip_offset + 16;
					rh = hip_offset - 16;
					lk = 0;
					rk = knee_mult * -12;
					break;
				case 53:
					lh = hip_offset + 14;
					rh = hip_offset - 14;
					lk = 0;
					rk = knee_mult * -13;
					break;
				case 54:
					lh = hip_offset + 12;
					rh = hip_offset - 12;
					lk = 0;
					rk = knee_mult * -14;
					break;
				case 55:
					lh = hip_offset + 10;
					rh = hip_offset - 10;
					lk = 0;
					rk = knee_mult * -15;
					break;
				case 56:
					lh = hip_offset + 8;
					rh = hip_offset - 8;
					lk = 0;
					rk = knee_mult * -16;
					break;
				case 57:
					lh = hip_offset + 6;
					rh = hip_offset - 6;
					lk = 0;
					rk = knee_mult * -17;
					break;
				case 58:
					lh = hip_offset + 4;
					rh = hip_offset - 4;
					lk = 0;
					rk = knee_mult * -18;
					break;
				case 59:
					lh = hip_offset + 2;
					rh = hip_offset - 2;
					lk = 0;
					rk = knee_mult * -19;
					break;
			}

			output.addProperty("left_hip", lh);
			output.addProperty("right_hip", rh);
			output.addProperty("left_knee", lk);
			output.addProperty("right_knee", rk);
			output.addProperty("y_offset", offset_y_targ);

			frames.add(output);
		}

		System.out.println(frames);
	}
}
