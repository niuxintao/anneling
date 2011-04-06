package simulating;

public class test {
	public double T;
	public double decrement;
	public test(double T,double decrement)
	{
		this.T=T;
		this.decrement=decrement;
	}
	public int  process()
	{
		//N��ʼ��Ϊ���Ƕ�
		int start=DataCenter.coveringArrayNum;
		int end = 0;
		boolean flag=false;
	  //���ַ����ҵ���С��N
		while(start>end||!flag)
		{
			if(start<=end)//��һ�������ҵ����ʵ�start��end
			{
				end=start;
				start*=2;
			}
			int middle=(start+end)/2;
			AnnelProcess al=new AnnelProcess(middle, T, decrement);
			al.startAnneling();
			if(al.isOk())
			{
				start=middle-1;
				flag=true;
			}
			else
				end=middle+1;
		}
		return start;
	}
	static public void main(String[] args) {
		int param[] = { 10, 10, 10,10,10,10 };
		DataCenter.init(param, 3);
		System.out.println(DataCenter.coveringArrayNum);
		test t=new test(1,0.9998);
		System.out.println("reslut: " + t.process());
	}
}
