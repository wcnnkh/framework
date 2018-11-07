package shuchaowen.excel;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class JxlUtils {
	private static SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 格式化时间
	 * 
	 * @param date
	 * @return 格式：yyyy-MM-dd HH:mm:ss
	 */
	public static String formate(Date date) {
		return dateFormate.format(date);
	}

	/**
	 * 格式化时间
	 * 
	 * @param dateMS
	 * @return 格式：yyyy-MM-dd HH:mm:ss
	 */
	public static String formate(long dateMS) {
		return formate(new Date(dateMS));
	}

	/**
	 * String "0" == null true<br>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		if ("NULL".equalsIgnoreCase(str) || null == str || "".equals(str.trim()) || 0 == str.trim().length()
				|| ("0".equals(str.trim()))) {
			return true;
		}
		return false;
	}

	/**
	 * normal string null<br>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull4Str(String str) {
		if ("NULL".equalsIgnoreCase(str) || null == str || "".equals(str.trim()) || 0 == str.trim().length()) {
			return true;
		}
		return false;
	}

	public static boolean isNull4db(String str) {
		if (null == str || "".equals(str.trim()) || ("0".equals(str.trim())) || "null".equals(str)
				|| "NULL".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断一组字符串中是否有空
	 * 
	 * @param strList
	 * @return
	 */
	public static boolean isNullAll(String... strList) {
		for (String str : strList) {
			if (null == str || str.trim().length() == 0) {
				return true;
			}
		}
		return false;
	}

	public static int parseInt(String str) {
		if (isNull(str)) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	public static long parseLong(String str) {
		if (isNull(str)) {
			return 0;
		}
		return Long.parseLong(str);
	}

	public static float parseFloat(String str) {
		if (isNull(str)) {
			return 0;
		}
		return Float.parseFloat(str);
	}

	public static boolean parseBoolean(String str) {
		if (str == null) {
			return false;
		}
		String t = str.trim();
		if ("true".equals(t)) {
			return true;
		} else if ("1".equals(t)) {
			return true;
		}
		return false;
	}

	/**
	 * 数据格式：key:value,key:value
	 * 
	 * @param str
	 * @return
	 */
	public static Map<Integer, Integer> parseMap(String str) {
		if (isNull(str)) {
			return null;
		}
		String[] arr = str.split(",");
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < arr.length; i++) {
			String[] keyValue = arr[i].split(":");
			int key = Integer.parseInt(keyValue[0]);
			int value = Integer.parseInt(keyValue[1]);
			map.put(key, value);
		}
		return map;
	}

	public static Map<Integer, int[]> parseMap2(String str) {
		if (isNull(str)) {
			return null;
		}
		String[] arr = str.split(",");
		Map<Integer, int[]> map = new HashMap<Integer, int[]>();
		for (int i = 0; i < arr.length; i++) {
			String[] keyValue = arr[i].split(":");
			int key = Integer.parseInt(keyValue[0]);
			int value1 = Integer.parseInt(keyValue[1]);
			int value2 = Integer.parseInt(keyValue[2]);
			map.put(key, new int[] { value1, value2 });
		}
		return map;
	}

	/**
	 * 数据格式：value,value
	 * 
	 * @param str
	 * @return
	 */
	public static int[] parseArr(String str) {
		if (isNull(str)) {
			return null;
		}
		String[] arr = str.split(",");
		int[] intArr = new int[arr.length];
		for (int i = 0; i < arr.length; i++) {
			intArr[i] = Integer.parseInt(arr[i]);
		}
		return intArr;
	}

	public static float[] parseFloatArr(String str) {
		if (isNull(str)) {
			return null;
		}
		String[] arr = str.split(",");
		float[] intArr = new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			intArr[i] = Float.parseFloat(arr[i]);
		}
		return intArr;
	}

	public static List<Integer> parseList(String str) {
		if (isNull(str)) {
			return null;
		}
		List<Integer> list = new ArrayList<Integer>();
		String[] arr = str.split(",");
		for (int i = 0; i < arr.length; i++) {
			int value = Integer.parseInt(arr[i]);
			list.add(value);
		}
		return list;
	}

	/**
	 * 格式a:b:c,d:e:f
	 * 
	 * @param str
	 * @return
	 */
	public static List<int[]> parseList2(String str) {
		if (isNull(str)) {
			return null;
		}
		List<int[]> list = new ArrayList<int[]>();
		String[] arr1 = str.split(",");
		for (int i = 0; i < arr1.length; i++) {
			String subStr = arr1[i].trim();
			String[] arr2 = subStr.split(":");
			list.add(new int[] { Integer.parseInt(arr2[0]), Integer.parseInt(arr2[1]), Integer.parseInt(arr2[2]) });
		}
		return list;
	}

	/**
	 * 数据格式：value,value
	 * 
	 * @param str
	 * @return
	 */
	public static String[] parseArr(String str, String split) {
		if (isNull(str)) {
			return null;
		}
		String[] arr = str.split(split);
		return arr;
	}

	public static String getContent(Sheet sheet, int rowIndex, int colIndex) {
		Cell[] cells = sheet.getRow(rowIndex);
		if (colIndex >= cells.length) {
			return "";
		} else {
			return cells[colIndex].getContents().trim();
		}
	}

	public static String Map2String(Map<Integer, Integer> map) {
		StringBuffer buf = new StringBuffer();
		if (map == null || map.size() == 0) {
			return "";
		}
		Iterator<Integer> it = map.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			int value = map.get(key);
			if (buf.length() > 0) {
				buf.append(",");
			}
			buf.append(key).append(":").append(value);
		}
		return buf.toString();
	}

	public static boolean isNum(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 将万分比转化为百分数
	 * 
	 * @param rate
	 * @return
	 */
	public static String rateToString(int rate) {
		int v = (int) (Math.abs(rate) * 0.01);
		return v + "%";
	}

	/**
	 * 追加哈希表
	 * 
	 * @param map
	 * @param appendMap
	 */
	public static void appendMap(Map<Integer, Integer> map, Map<Integer, Integer> appendMap) {
		if (appendMap != null) {
			Iterator<Integer> it = appendMap.keySet().iterator();
			while (it.hasNext()) {
				int key = it.next();
				int value = appendMap.get(key);
				if (!map.containsKey(key)) {
					map.put(key, value);
				} else {
					int oldValue = map.get(key);
					map.put(key, oldValue + value);
				}
			}
		} // end if
	}

	public static Map<Integer, Integer> getNewMap(Map<Integer, Integer> map, int plus) {
		Map<Integer, Integer> newMap = new HashMap<Integer, Integer>();
		if (map != null) {
			Iterator<Integer> it = map.keySet().iterator();
			while (it.hasNext()) {
				int key = it.next();
				int value = map.get(key);
				int newValue = value * plus;
				newMap.put(key, newValue);
			} // end while
		} // end if
		return newMap;
	}

	public static void appendMap(Map<Integer, Integer> map, Map<Integer, Integer> appendMap, int count) {
		if (appendMap != null) {
			Iterator<Integer> it = appendMap.keySet().iterator();
			while (it.hasNext()) {
				int key = it.next();
				int value = appendMap.get(key);
				value = value * count;
				if (!map.containsKey(key)) {
					map.put(key, value);
				} else {
					int oldValue = map.get(key);
					map.put(key, oldValue + value);
				}
			}
		} // end if
	}

	/**
	 * 检测村庄名称,只能中\英文\数字
	 * 
	 * @param villageName
	 * @return
	 */
	public static boolean checkName(String name) {
		String reg = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{1,12}$";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(name);
		return m.matches();
	}

	public static void exportExcel(HttpServletResponse response, List<Object[]> list, int columnNum, String fileName,
			String sheetName) {
		try {
			OutputStream os = response.getOutputStream();// 取得输出流
			response.reset();// 清空输出流
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel");// 定义输出类型

			WritableWorkbook wbook = Workbook.createWorkbook(os); // 建立excel文件
			WritableSheet wsheet = wbook.createSheet(sheetName, 0); // sheet名称

			// 开始生成主体内容
			wsheet.addCell(new Label(0, 2, "城市代码"));
			wsheet.addCell(new Label(1, 2, "城市名"));

			// 主体内容生成结束
			wbook.write();// 写入文件
			wbook.close();
			os.close();// 关闭流
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String List2String(List<Integer> list) {
		StringBuffer buf = new StringBuffer();
		if (list == null || list.size() == 0) {
			return "";
		}

		for (Object obj : list) {
			if (0 < buf.length()) {
				buf.append(",");
			}
			buf.append(obj);
		}
		return buf.toString();
	}
}