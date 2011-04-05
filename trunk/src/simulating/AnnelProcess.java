package simulating;

import java.util.Random;
import annInterface.AnnelInf;
import annInterface.CoveringManagementInf;

public class AnnelProcess implements AnnelInf {
	public int[] coveringArray; // ���Ǽ�¼����һ�������ֵ��ζ�ű����ǵĴ���
	public Integer unCovered;// δ�����Ƕ�
	public int freezingTimes;// ��ס�Ĵ���
	public int[][] table; // N*K�ı�
	private Random randomGenerator = new Random(); // ���������
	private int rowChange;// �ı����
	private int[] oldRow; // �ı�ǰ����
	private int[] newRow; // �ı�����
	private boolean feasiable;// �Ƿ����
	private int N; // ������СN
	private double T;// �¶�T
	private double decrement;// �����½�

	public AnnelProcess(int N, double T, double decrement) {
		this.N = N;
		this.T = T;
		this.decrement = decrement;
	}

	@Override
	public void initAnneling() {
		// TODO Auto-generated method stub
		// �������һ��N*K�ı�`
		this.feasiable = false;
		CoveringManagementInf cm = new CoveringManage();
		// �������һ��N*K�ı�
		// ��ʼ��coveringArray
		this.coveringArray = new int[DataCenter.coveringArrayNum];
		unCovered = this.coveringArray.length;
		this.freezingTimes = 0;
		table = new int[N][DataCenter.param.length];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < DataCenter.param.length; j++)
				table[i][j] = randomGenerator.nextInt(DataCenter.param[j]);
			unCovered=cm.setCover(unCovered, coveringArray, table[i]);
		}
	}

	@Override
	public boolean isAccept() {
		// TODO Auto-generated method stub
		CoveringManagementInf cm = new CoveringManage();
		int oldUncovered = unCovered.intValue();
		unCovered=cm.rmCover(unCovered, coveringArray, oldRow);
		unCovered=cm.setCover(unCovered, coveringArray, newRow);
		if (oldUncovered > unCovered) {
			freezingTimes = 0;
			return true;
		} else if (oldUncovered == unCovered) {
			freezingTimes++;
			return true;
		} else // �Ը���p����
		{
			freezingTimes++;
			double p = Math.pow(Math.E, -(unCovered - oldUncovered) / this.T);
			double exa = randomGenerator.nextDouble();
			if (exa < p)
				return true;
			else
				return false;
		}
	}

	@Override
	public boolean isEnd() {
		// TODO Auto-generated method stub
		if (unCovered == 0) {
			this.feasiable = true;
			return true;
		} else if (freezingTimes > DataCenter.maxFreeing) {
			this.feasiable = false;
			return true;
		}
		return false;
	}

	@Override
	public void makeChange() {
		// TODO Auto-generated method stub
		// ��������һ��cell�ı���ֵ
		this.rowChange = randomGenerator.nextInt(N);
		int col = randomGenerator.nextInt(DataCenter.param.length);
		int newValue = (table[rowChange][col] + 1) % DataCenter.param[col];
		oldRow = table[rowChange];
		newRow = table[rowChange].clone();
		newRow[col] = newValue;
	}

	@Override
	public void startAnneling() {
		// TODO Auto-generated method stub
		// init
		this.initAnneling();
		// do
		while (!isEnd())// ���û�н�����һֱ��
		{
			this.makeChange();
			if (this.isAccept())// �������
				table[rowChange] = this.newRow;
			else {
				CoveringManagementInf cm = new CoveringManage();
				unCovered=cm.rmCover(unCovered, coveringArray, newRow);
				unCovered=cm.setCover(unCovered, coveringArray, oldRow); // ����
			}
			this.T = this.T * this.decrement;
		}
	}

	@Override
	public boolean isOk() {
		// TODO Auto-generated method stub
		return this.feasiable;
	}

	public static void main(String[] args) {

	}

}
