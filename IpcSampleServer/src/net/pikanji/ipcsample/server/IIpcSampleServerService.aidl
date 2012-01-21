package net.pikanji.ipcsample.server;

interface IIpcSampleServerService {
    void setData(int data);
    int getData();
    int getPid();
}