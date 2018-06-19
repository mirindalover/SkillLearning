

```Java

 public String replace(char oldChar, char newChar) {
	if (oldChar != newChar) {
		int len = value.length;
		int i = -1;
		char[] val = value; /* avoid getfield opcode */

		//先找到第一个需要替换的char
		while (++i < len) {
			if (val[i] == oldChar) {
				break;
			}
		}
		if (i < len) {
			char buf[] = new char[len];
			//创建新的数组-----这里体现了String的不可更改性
			for (int j = 0; j < i; j++) {
				buf[j] = val[j];
			}
			// 如果char相同就替换
			while (i < len) {
				char c = val[i];
				buf[i] = (c == oldChar) ? newChar : c;
				i++;
			}
			return new String(buf, true);
		}
	}
	return this;
}

```

### 方法借鉴

不需要一开始就创建char[] ，先找到第一个需要替换的index。这样可以避免  没有替换的情况


思路：根据逻辑一步步走即可。



```Java

static int indexOf(char[] source, int sourceOffset, int sourceCount,
            char[] target, int targetOffset, int targetCount,
            int fromIndex) {
	if (fromIndex >= sourceCount) {
		return (targetCount == 0 ? sourceCount : -1);
	}
	if (fromIndex < 0) {
		fromIndex = 0;
	}
	if (targetCount == 0) {
		return fromIndex;
	}

	char first = target[targetOffset];
	int max = sourceOffset + (sourceCount - targetCount);

	for (int i = sourceOffset + fromIndex; i <= max; i++) {
		/* Look for first character. */
		//与String.replace相同，都是先找第一个符合的char
		if (source[i] != first) {
			while (++i <= max && source[i] != first);
		}

		/* Found first character, now look at the rest of v2 */
		if (i <= max) {
			int j = i + 1;
			int end = j + targetCount - 1;
			//再根据count来对比后面的其余char
			for (int k = targetOffset + 1; j < end && source[j]
					== target[k]; j++, k++);

			if (j == end) {
				/* Found whole string. */
				return i - sourceOffset;
			}
		}
	}
	return -1;
}

```

