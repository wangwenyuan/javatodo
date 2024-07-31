package com.javatodo.core.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IDGenerator {
	public static IDGenerator Id = new IDGenerator();
	private long lastStmp = -1L;// 上一次时间戳
	private long sequence = 0L; // 序列号
	private long MAX_SEQUENCE = 99L;

	/**
	 * 产生下一个ID
	 *
	 * @return
	 */
	public synchronized String nextId() {
		long currStmp = getNewstmp();
		if (currStmp < lastStmp) {
			throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
		}

		if (currStmp == lastStmp) {
			// 相同毫秒内，序列号自增
			if (sequence + 1 < MAX_SEQUENCE) {
				sequence = sequence + 1;
			} else {
				currStmp = getNextMill();
				sequence = 0L;
			}
		} else {
			// 不同毫秒内，序列号置为0
			sequence = 0L;
		}
		lastStmp = currStmp;
		if (sequence < 10) {
			return currStmp + "0" + sequence;
		} else {
			return currStmp + "" + sequence;
		}

	}

	private long getNextMill() {
		long mill = getNewstmp();
		while (mill <= lastStmp) {
			mill = getNewstmp();
		}
		return mill;
	}

	private long getNewstmp() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String shijian = df.format(new Date());
		shijian = shijian + (System.currentTimeMillis() + "").substring(10);
		return Long.valueOf(shijian);
	}

	public static void main(String[] args) {
		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < (1 << 18); i++) {
			String id = Id.nextId();
			if (idList.contains(id)) {
				System.err.println(id + "已经存在");
			} else {
				System.out.println(id);
			}
		}
		System.out.println("生成完毕");
	}
}
