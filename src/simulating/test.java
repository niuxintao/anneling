package simulating;

public class test {
	public double T;
	public double decrement;
	public test(double T,double decremnet)
	{
		this.T=T;
		this.decrement=decremnet;
		//N初始化为覆盖对
		int N=DataCenter.coveringArrayNum;
	}
	static public void main(String[] args) {
		int param[] = { 10, 10, 10 ,10,10,10};
		DataCenter.init(param, 3);
		System.out.println(DataCenter.coveringArrayNum);
		AnnelProcess al = new AnnelProcess(2000, 1, 0.9998);
		al.startAnneling();
		System.out.println("reslut: " + al.isOk());
	}
}
