// generated with ast extension for cup
// version 0.8
// 7/1/2024 11:17:34


package rs.ac.bg.etf.pp1.ast;

public class NoCompare extends OptExpr {

    public NoCompare () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NoCompare(\n");

        buffer.append(tab);
        buffer.append(") [NoCompare]");
        return buffer.toString();
    }
}
