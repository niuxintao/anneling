package simulating;

public class test {
	static public void main(String[] args) {
		int param[] = { 10, 10, 10 ,10};
		DataCenter.init(param, 2);
		System.out.println(DataCenter.coveringArrayNum);
		AnnelProcess al = new AnnelProcess(200, 1, 0.9998);
		al.startAnneling();
		System.out.println("reslut: " + al.isOk());
	}
}
