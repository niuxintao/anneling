package annInterface;

public interface AnnelInf {
	public void initAnneling(); //初始化退火
	public void startAnneling();//开始退火
	public void makeChange();  //做一个小的变动
	public boolean isEnd();    //退火过程是否结束
	public boolean isAccept();  //这个改动是否被接受
	public boolean isOk();     //在这个情况下是否得到一个解集
}
