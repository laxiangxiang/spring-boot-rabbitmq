package com.rabbitmqandwebsocketexample.demo.dto;

import java.io.Serializable;

public class TestUser implements Serializable{
    private String name;
    private Integer age;

    public TestUser(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public TestUser() {
    }

    public TestUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestUser{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
