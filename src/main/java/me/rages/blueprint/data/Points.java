package me.rages.blueprint.data;


/**
 * @author : Michael
 * @since : 6/18/2022, Saturday
 **/
public class Points<L, R> {

    private L min;
    private R max;

    public Points(L key, R value) {
        this.min = key;
        this.max = value;
    }

    public L getMin() {
        return min;
    }

    public R getMax() {
        return max;
    }

    public void setMax(R max) {
        this.max = max;
    }

    public void setMin(L min) {
        this.min = min;
    }

    public String toString() {
        return "" + '(' + this.getMin() + ',' + this.getMax() + ')';
    }

}