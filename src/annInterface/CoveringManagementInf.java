package annInterface;

public interface CoveringManagementInf {
	public int setCover(Integer unCovered,int[] coveringArray,int[] row); //增加这一行
	public int rmCover(Integer unCovered,int[] coveringArray,int[] row);//减少这一行
}
