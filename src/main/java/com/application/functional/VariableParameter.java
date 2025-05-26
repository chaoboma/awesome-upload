package com.application.functional;

import java.util.Optional;

public class VariableParameter {
    public static void printNumbers(int... numbers) {
        for (int number : numbers) {
            System.out.println(number);
        }
    }
    public static void f(Integer type){
        if(type == null) {
            type = 1;
        }


    }


    public static void main(String[] args) {
        //printNumbers(1,2,3);
        Integer type  = null;
        Integer result  = Optional.ofNullable(type).orElse(1);
        System.out.println("result:"+result);
    }

}
