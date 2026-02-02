package me.freznel.compumancy.current;

public interface ICurrentProvider {

    public double GetMaximum();
    public double GetCurrent();

    public double AddCurrent(double amount);
    public double RemoveCurrent(double amount);

}
