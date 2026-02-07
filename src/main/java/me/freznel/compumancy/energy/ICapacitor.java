package me.freznel.compumancy.energy;

public interface ICapacitor {

    public double getCapacity();
    public double getCharge();

    public double add(double amount);
    public default double remove(double amount) { return add(-amount); }

}