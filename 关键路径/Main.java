package 关键路径;

import java.util.Stack;

/*
 * 使用邻接表的方式来进行存储，在邻接表结点的基础上添加了前驱个数属性
 * 根据前驱之间的关系进行排序输出，输出规则，只有把前驱结点输出之后才能输出后面的元素
 */
public class Main {
	private HeadNode[] headNode; // 节点个数

	public int[] ve;// 每个节点最早开始时间
	public int[] vl;// 每个节点最晚开始时间

	private static final int MAX = Integer.MAX_VALUE; // 最大值表示当前最晚开始时间的初始状态

	public Stack<Integer> stack;// 用于存储拓扑矩阵的栈

	public Stack<Integer> operatorStack;// 用于最短路径中存储的栈

	// 头结点信息
	public class HeadNode {
		public char node; // 头结点信息

		public NextNode next; // 下一结点信息

		public int inDegree; // 前驱的个数
		public int weight; // 当前边的权值信息

		public HeadNode(char node, NextNode next, int inDegree, int weight) {
			this.node = node;
			this.next = next;
			this.inDegree = inDegree;
			this.weight = weight;
		}

	}

	// 与头结点相连接的下一结点信息
	public class NextNode {
		public int index; // 当前节点所在数组中的位置

		public NextNode next; // 下一结点信息

		public int weight; // 当前边的权值信息
		public int inDegree; // 入度

		public NextNode(int index, NextNode next, int weight, int inDegree) {
			this.index = index;
			this.next = next;
			this.weight = weight;
			this.inDegree = inDegree;
		}
	}

	// 初始化数组结点
	public void initNode(char[] node) {
		if (node == null) {
			return;
		}
		headNode = new HeadNode[node.length];// 初始化头结点数组长度与传入node长度相同
		for (int i = 0; i < node.length; i++) {
			HeadNode head = new HeadNode(node[i], null, 0, 0);
			headNode[i] = head;
		}
		ve = new int[node.length];
		vl = new int[node.length];
		for (int i = 0; i < node.length; i++) {
			ve[i] = 0;
			ve[i] = MAX;
		}
	}

	// 初始化整个邻接表
	public void initTable(char[][] edgs) {
		for (int i = 0; i < edgs.length; i++) {
			char begin = edgs[i][0];// 起始值
			char end = edgs[i][1];// 终止值
			int index = 0;// 终止值在初始化数组中的索引位置
			// 在数组当中找到索引位置
			for (int j = 0; j < headNode.length; j++) {
				if (headNode[j].node == end) {
					index = j;
					break;
				}
			}
			NextNode next = new NextNode(index, null, 0, 0);// 生成下一个节点的包装类
			// 为数组中的头结点进行串线
			for (int j = 0; j < headNode.length; j++) {
				if (headNode[j].node == begin) {
					NextNode temp = headNode[j].next;// 操作变量
					headNode[index].inDegree++; // 当前节点有一个前驱了进行++操作
					// 如果头结点的下一节点为空的话就让当前生成的包装类节点赋予给我们的下一节点
					if (temp == null) {
						headNode[j].next = next;
					} else {
						// 判断当前头结点下面有没有连接着其他的节点
						while (temp.next != null) {
							temp = temp.next;
						}
						temp.next = next;
					}
				}
			}
		}
	}

	// 打印函数
	public void print(HeadNode[] head) {
		for (int i = 0; i < head.length; i++) {
			System.out.print(head[i].node);
			NextNode temp = head[i].next;
			while (temp != null) {
				System.out.print(" --> ");
				System.out.print(temp.index);
				temp = temp.next;
			}
			System.out.println();
		}
		System.out.println();
	}

	// 打印各个结点的前驱个数
	public void printDegree() {
		for (int i = 0; i < headNode.length; i++) {
			System.out.println("当前节点为:" + headNode[i].node + "包含前驱有["
					+ headNode[i].inDegree + "]个");
		}
		System.out.println();
	}

	/*
	 * 拓扑排序 思路：每次把前驱为0的结点给输出出去，然后把与之相连的边全部去掉，之后在重复操作
	 * 邻接表中用于判断结点中的inDegree属性，如果为0输出，当去除调这个边的时候就是把与这个结点直接相连的
	 * 结点中的inDegree减一操作。重复进行判断
	 */

	public void topological() {
		// 为什么要创建这个数组是因为我们要去修改节点的前驱个数，如果直接操作节点会导致拓扑排序后图的结构被改变了
		// 这里值的结构也就是每个节点中的原始前驱个数
		int[] inDegree = new int[headNode.length];// 创建每个结点的入度数组
		int count = 0;// 当前已遍历节点的个数
		// 里面的存储类型是头结点类型
		stack = new Stack<Integer>();
		// 轮询整个结点数组当前驱为0的时候让他入栈
		for (int i = 0; i < headNode.length; i++) {
			inDegree[i] = headNode[i].inDegree;// 把当前节点的前驱个数赋予给我们的数组当中去
			if (inDegree[i] == 0) {
				stack.push(i);
				operatorStack.push(i);
			}
		}
		while (!stack.isEmpty()) {
			int index = stack.pop();// 弹出结点并输出
			count++;// 遍历一个节点了加加操作
			System.out.print(headNode[index].node + " ");
			// 把与当前节点相连的节点入度均减一，如果入度为0说明可进入栈中
			for (NextNode next = headNode[index].next; next != null; next = next.next) {
				int k = next.index;
				if (--inDegree[k] == 0) {
					stack.push(next.index);
					operatorStack.push(next.index);
				}
				// 如果前一个节点加上当前节点的权值比当前节点最早开始时间大的话我们 就让当前节点的最早开始值为这个
				if (ve[index] + next.weight > ve[k]) {
					ve[k] = ve[index] + next.weight;
				}
			}
		}
		if (count < headNode.length) {
			System.out.println("当前并不能构成拓扑排序因为有节点并没有与其他节点相连");
		}
	}

	// 获取关键路径的方法
	public void getCriticalPath() {
		// 调用拓扑矩阵方法，初始化每个节点的最早开始时间，栈中保存元素
		topological();
		int lastIndex = operatorStack.peek();// 当前的汇点,查看当前点但并不移除
		vl[lastIndex] = ve[lastIndex];// 汇点的最早开始时间和最晚开始时间相同，因为此点为关键路径上的点
		while (!operatorStack.isEmpty()) {
			int current = operatorStack.pop();// 取出当前汇点的索引
			// 找出当前与汇点相连接的点
			for (NextNode next = headNode[current].next; next != null; next = next.next) {
				int after = next.index; // 相连点的索引
				// 如果前一个节点最晚开始时间减去前一个节点的权值比当前最晚开始时间要小则修改
				if (vl[after] - next.weight < vl[current]) {
					vl[current] = vl[after] - next.weight;
				}
			}
		}
		// 判断两个节点的最早开始时间和最晚开始时间是否相同，如果相同则为关键路径上的节点
		for (int i = 0; i < ve.length; i++) {
			for (NextNode next = headNode[i].next; next != null; next = next.next) {
				int k = next.index;
				int ee = ve[i];
				int el = vl[k] - next.weight;
				if (ee == el) {
					System.out.print(i + "," + k + "     ");
				}
			}
		}
	}

	public static void main(String[] args) {
		char[] node = { 'A', 'B', 'C', 'D', 'E', 'F' };
		char[][] edgs = new char[][] { { 'A', 'B' }, { 'A', 'C' },
				{ 'A', 'D' }, { 'B', 'E' }, { 'C', 'E' }, { 'C', 'F' },
				{ 'D', 'E' }, { 'D', 'F' }, };
		Main m = new Main();
		// 初始化数组头结点的过程
		m.initNode(node);
		// 初始化邻接表的过程
		m.initTable(edgs);
		// 打印结果
		m.print(m.headNode);
		// 打印每个结点的前驱个数
		m.printDegree();
		// 拓扑排序
		m.topological();
	}
}
