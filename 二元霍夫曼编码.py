#coding:utf-8
from PIL import Image
from struct import  *
xiang_su_pin_lv={}
jie_dian_list = []
bian_ma_biao = {}

class node: #节点的类
    def __init__(self, right=None, left=None, parent=None, weight=0, code=None):  #节点构造方法
        self.left = left
        self.right = right
        self.parent = parent
        self.weight = weight
        self.code = code

def picture_convert(): #将bmp图片转换为灰值图
    picture = Image.open('./test.bmp')
    picture = picture.convert('L')
    picture.save('new.bmp')
    return picture  #返回转换后的图片对象

def pin_lv_tong_ji(list): #统计每个像素出现的次数
    global xiang_su_pin_lv
    for i in list:
        if i not in xiang_su_pin_lv.keys():
            xiang_su_pin_lv[i]=1 #若此像素点不在字符频率字典里则直接添加
        else:
            xiang_su_pin_lv[i] += 1 #若存在在字符频率字典里则对应值加一


def gou_zao_ye_zi(xiang_su_zhi): #构造叶子节点，分别赋予其像素点的值和像素点的权值
    for i in range(len(xiang_su_zhi)):
        jie_dian_list.append(node(weight=xiang_su_zhi[i][1], code=str(xiang_su_zhi[i][0])))
        #print xiang_su_zhi[i][0],str(xiang_su_zhi[i][1])
    #print jie_dian_list.__len__()
    return jie_dian_list

def sort_by_weight(list_node):  #将每次更新后的node列表按权值进行排序
    list_node =sorted(list_node, key=lambda node:node.weight)
    return list_node

def huo_fu_man_shu(listnode): #构造霍夫曼树
    listnode = sort_by_weight(listnode)
    while len(listnode) != 1:
        low_node0,low_node1 = listnode[0], listnode[1] #每次取最小权值的两个像素点进行合并
        new_change_node = node()
        new_change_node.weight = low_node0.weight + low_node1.weight
        new_change_node.left = low_node0
        new_change_node.right = low_node1
        low_node0.parent = new_change_node
        low_node1.parent = new_change_node
        listnode.remove(low_node0)
        listnode.remove(low_node1)
        listnode.append(new_change_node)
        listnode = sort_by_weight(listnode)
    #print listnode
    return listnode #返回头结点

def er_yuan_huo_fu_man_bian_ma(picture):  #编码函数，返回编码表以及编码结果
    width = picture.size[0]
    height = picture.size[1]
    im = picture.load()
    print ("像素点个数为："),width*height
    print ("灰度图宽为"+str(width)+"像素")
    print ("灰度图高为"+str(height)+"像素")
    list = [] #将像素点保存在列表中进行频率统计
    for i in range(width):
        for j in range(height):
           list.append(im[i,j])
    pin_lv_tong_ji(list) #统计每个像素点的次数
    global xiang_su_pin_lv
    xiang_su = xiang_su_pin_lv
    xiang_su = sorted(xiang_su.items(),key=lambda item:item[1])
    ye_zi_list = gou_zao_ye_zi(xiang_su) #构造叶子节点
    head = huo_fu_man_shu(ye_zi_list)[0] #保存编码树的头结点
    global  bian_ma_biao
    for e in ye_zi_list:  #构造编码表
        new_change_node = e
        bian_ma_biao.setdefault(e.code, "")
        while new_change_node!=head:
            if new_change_node.parent.left == new_change_node:
                bian_ma_biao[e.code] = "1" + bian_ma_biao[e.code]
            else:
                bian_ma_biao[e.code] = "0" + bian_ma_biao[e.code]
            new_change_node = new_change_node.parent
    for key in bian_ma_biao.keys():
        print ("信源像素点" + key+"霍夫曼编码后的码字为：" + bian_ma_biao[key])
    result = '' #编码结果，对每个像素点进行霍夫曼编码
    for i in range(width):
        for j in range(height):
            for key,values in bian_ma_biao.iteritems():
                if str(im[i,j]) == key:
                    result = result+values
    file = open('result.txt','w')
    file.write(result)
    print "大佬，您的编码表为:",bian_ma_biao

def zi_jie_xie_ru():
    pppp = open('result.txt', 'r')
    pppp = pppp.readlines()[0].strip('\n')
    str = pppp
    yu_shu = 8 - pppp.__len__() % 8
    #print yu_shu
    file = open("huo_fu_man_compress.txt", "wb")
    for i in range(0,str.__len__(),8):
        if i+8>str.__len__():
            char = 00000000
            erjinzhi = str[i:]
            #print erjinzhi
            buling = ''
            for i in range(yu_shu):
                buling = buling + '0'
            erjinzhi = buling +erjinzhi

            for j in range(8):
                if erjinzhi[j] == '1':
                    if j == 7:
                        char += 1
                    else:
                        char += 1
                        char = char << 1
                if erjinzhi[j] == "0":
                    if j == 7:
                        pass
                    else:
                        char = char << 1
            file.write(pack("B", char))
            break
        erjinzhi=str[i:i+8]
        char = 00000000
        for j in range(8):
            if erjinzhi[j] == '1':
                if j == 7:
                    char += 1
                else:
                    char +=1
                    char = char << 1
            if erjinzhi[j] == "0":
                if j ==7:
                    pass
                else:
                    char = char << 1
        file.write(pack("B", char))
    file.write(pack('B',yu_shu))
    print ("大佬，您的编码已经完成：") + ("  二元霍夫曼编码结果已经存到huo_fu_man_compress.txt中")
if __name__ == '__main__':
    picture = picture_convert()
    er_yuan_huo_fu_man_bian_ma(picture)
    zi_jie_xie_ru()
