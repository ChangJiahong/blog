package com.cjh.blog.exce;

/**
 * 没有找到这样的数据
 * @author ChangJiahong
 * @date 2019/5/29
 */
public class NoSuchFoundException extends MyException{

    public NoSuchFoundException(){
        super();
    }

    public NoSuchFoundException(String msg){
        super(msg);
    }
}
