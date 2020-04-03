import java.io.*;
import java.util.*;

/**
 * @author tr1ple
 */

class node{
    /**
     * 节点类
     */
    public node paraent;
    public node left;
    public node right;
    public String code;
    public float weight;

    node(node paraent, node left, node right, String code, float weight){
        this.paraent = paraent;
        this.left = left;
        this.right = right;
        this.code = code;
        this.weight = weight;
    }
}
public class encode {
    /**
     charset : 文本字符集
     charFrequency : 字符出现频率
     charCount :  字符出现次数
     */
    public HashMap<String, Float> charFrequency;
    public HashMap<String, Integer> charCount;
    public int strLength=0;
    public ArrayList<node> nodeList;
    public HashMap<String, String> codingTable;

    private encode(){
    this.charCount = new HashMap<String, Integer>();
    this.charFrequency = new HashMap<String,Float>();
    this.nodeList = new ArrayList<node>();
    this.codingTable = new HashMap<String, String>();
    }

    public String getStr() throws IOException {
        FileInputStream file = new FileInputStream(new File(System.getProperty("user.dir")+"/javasec-basic/src/main/resources/origin.txt"));
        int size = file.available();
        byte[] strByte= new byte[size];
        file.read(strByte);
        return new String(strByte);
    }

    /**
     * todo : 统计字符出现次数
     * @param str
     */
    public void calcCount(String str){
        int len = str.length();
        for(int i=0;i<len;i++){
            String tmp = String.valueOf(str.charAt(i));
            if(this.charCount.containsKey(tmp)){
                this.charCount.put(tmp,this.charCount.get(tmp)+1);
            }else{
                this.charCount.put(tmp,1);
                this.strLength= this.strLength + 1;
            }
        }
    }

    /**
     * todo : 计算字符频率
     */
    public void calcFrequency(){
        int len = this.charCount.size();
        Set<String> ketList = this.charCount.keySet();
        Iterator<String> keyIte = ketList.iterator();
        while(keyIte.hasNext()){
            String tmp = keyIte.next();
            float frequency = (float) this.charCount.get(tmp)/this.strLength;
            this.charFrequency.put(tmp,frequency);
        }
    }

    /**
     *  todo : 生成叶子节点
     */
    public void generateNode(){
        Set<String> keyList = this.charFrequency.keySet();
        Iterator<String> keyIte = keyList.iterator();
        while (keyIte.hasNext()){
            String tmp = keyIte.next();
            float weight =  this.charFrequency.get(tmp);
            this.nodeList.add(new node(null,null,null,tmp,weight));
        }
    }

    /**
     * todo : 权值大小判断
     * @param weight1
     * @param weight2
     * @return
     */
    public int isMax(float weight1,float weight2){
        float tmp = weight1 - weight2;
        if(tmp < 0){
            return -1;
        }
        else {
            return 1;
        }
    }

    /**
    todo : 节点排序
     */
    public List<node> nodeSort(List<node> tmpList){
        Collections.sort(tmpList, new Comparator<node>() {
            public int compare(node o1, node o2) {
                return isMax(o1.weight,o2.weight);
            }
        });
        return tmpList;
    }

    /**
     * todo : 生成霍夫曼树并返回根节点
     * @return
     */
    public node huffmanCoding(){
        List<node> tmp = this.nodeList;
        while(tmp.size()>1){
            tmp = nodeSort(tmp);
            node leaf1 = tmp.get(0);
            node leaf2 = tmp.get(1);
            float newWeight = leaf1.weight + leaf2.weight;
            node newNode = new node(null,leaf1,leaf2,"",newWeight);
            tmp.remove(leaf1);
            tmp.remove(leaf2);
            tmp.add(newNode);
        }
        return tmp.get(0);
    }

    /**
     * todo : 将字符换根据编码表进行编码
     * @param str
     * @return
     */
    public String encodingStr(String str){
        int length = str.length();
        String resultStr="";
        for(int i=0;i<length;i++){
            String chr = String.valueOf(str.charAt(i));
            resultStr = resultStr + this.codingTable.get(chr);
        }
        return resultStr;
    }

    /**
     *  todo : 先序遍历二叉树生成编码表
     * @param root
     * @param code
     * @return
     */
    public void recursionPreorderTraversal(node root,String prefix,HashMap<String, String> codingTable) {
        /*
         * todo ： 如果到了叶子节点，则将编码结果放入hashmap
         */
        if (root.left == null & root.right==null) {
            String leafName = root.code;
            codingTable.put(leafName,prefix);
            return;
        }
        recursionPreorderTraversal(root.left,prefix + "0" , codingTable);
        recursionPreorderTraversal(root.right,prefix + "1" , codingTable);
    }

    /**
     * todo : 输出编码后的字符串
     * @param str
     * @throws IOException
     */
    public void outputStr(String str) throws IOException {
        FileOutputStream fo = new FileOutputStream(new File(System.getProperty("user.dir")+"/javasec-basic/src/main/resources/result.txt"));
        fo.write(str.getBytes());
        System.out.println("[*] 编码结果输出路径为: ");
        System.out.println(System.getProperty("user.dir")+"/javasec-basic/src/main/resources/result.txt");
    }

    /**
     * todo : 生成编码表
     *
     */
    public void generateCodingTable(node root){
            recursionPreorderTraversal(root,"",this.codingTable);
    }

    /**
     * todo : 输出编码表
     */
   public void outputTable() throws IOException {
       FileOutputStream fo = new FileOutputStream(new File(System.getProperty("user.dir")+"/javasec-basic/src/main/resources/table.txt"));
       Set<String> keys = this.codingTable.keySet();
       Iterator ite = keys.iterator();
       while(ite.hasNext()){
           String tmp = (String)ite.next();
       fo.write((tmp+"="+this.codingTable.get(tmp)+"|").getBytes());
       }
       System.out.println(System.getProperty("user.dir")+"/javasec-basic/src/main/resources/table.txt");
       fo.close();
   }

    public static void main(String[] args) throws IOException {
        encode obj = new encode();
        String str = obj.getStr();
        obj.calcCount(str);
        obj.calcFrequency();
        System.out.println("[*] 字符出现次数统计:");
        System.out.println(obj.charCount);
        System.out.println("[*] 字符出现频率统计:");
        System.out.println(obj.charFrequency);
        obj.generateNode();
        node root = obj.huffmanCoding();
        obj.generateCodingTable(root);
        System.out.println("[*] 生成的编码表为:");
        System.out.println(obj.codingTable);
        String resultrStr = obj.encodingStr(str);
        System.out.println("[*] 编码结果为:");
        System.out.println(resultrStr);
        obj.outputStr(resultrStr);

        System.out.println("[*] 编码表输出路径为:");
        obj.outputTable();
    }

}
