package com.xcg.freshcommon.core.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T>{

    private int code;

    private T data;

    private String message;


    public static <T> Result<T> success(T data){
        return new Result<>(200,data,"操作成功");
    }

    public static <T> Result<T> success(){
        return new Result<>(200,null,"操作成功");
    }

    public static <T> Result<T> error(int code,String message){
        return new Result<>(code,null,message);
    }

    public static <T> Result<T> error(String message){
        return new Result<>(500,null,message);
    }
}