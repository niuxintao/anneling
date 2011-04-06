package simulating;

import java.util.Date;

public class Process {
	public double T;
	public double decrement;
	public long time;
	public int[][] rsTable;
	public Process(double T,double decrement)
	{
		this.T=T;
		this.decrement=decrement;
	}
	public void  process()
	{
		//N初始化为覆盖对
		int start=DataCenter.coveringArrayNum;
		int end = 0;
		boolean flag=false;
		long starttime=new Date().getTime();
	  //二分法来找到最小的N
		while(start>end||!flag)
		{
			if(start<=end)//这一条可以找到合适的start和end
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
				rsTable=al.table;
				flag=true;
			}
			else
				end=middle+1;
		}
		long endtime=new Date().getTime();
		time=endtime-starttime;
	}
	static public void main(String[] args) {
		int param[] = { 10, 10, 10, 10, 10, 10, 10 };
		DataCenter.init(param, 2);
		System.out.println(DataCenter.coveringArrayNum);
		Process t=new Process(2,0.9998);
		t.process();
		System.out.println("arrayLength: " + t.rsTable.length);
		System.out.println("time: "+t.time+ " ms");
	}
}
