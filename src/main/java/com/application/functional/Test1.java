package com.application.functional;


public class Test1 {

    private int a = 1;
    private int b = 2;
    /**
     * 这个方法需要一个IAdd类型的参数
     * @param add
     * @return
     **/
    public int add1(IAdd add){
        return add.add(a,b);
    }
    public static void main(String[] args) {
        Test1 test1 = new Test1();
        // 使用lambda表达式
        int c = test1.add1((a, b) -> a + b);
        System.out.println(c);
        // 使用匿名类
        int d = test1.add1(new IAdd(){
            @Override
            public int add(int a, int b){
                return a + b;
            }
        });
        System.out.println(d);
    }
}
