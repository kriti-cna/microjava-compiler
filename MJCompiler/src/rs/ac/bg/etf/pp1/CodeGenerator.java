package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.Tab;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.mj.runtime.Code;

import java.util.*;

public class CodeGenerator extends VisitorAdaptor {

    int mainPc;
    String currNamespace;

    public void visit(NamespaceName namespaceName) {
        currNamespace = namespaceName.getNspName();
    }

    public void visit(Namespace namespace) {
        currNamespace = "";
    }

    
    public void visit(MultipleTerms multipleTerms) {
        SyntaxNode type = multipleTerms.getMulop();
        if (type instanceof Multiply)
            Code.put(Code.mul);
        else if (type instanceof Divide)
            Code.put(Code.div);
        else if (type instanceof Modulo)
            Code.put(Code.rem);
    }


    public void visit(AddExpression addExpression) {
        SyntaxNode operation = addExpression.getAddop();
        if (operation instanceof Plus)
            Code.put(Code.add);
        if (operation instanceof Minus)
            Code.put(Code.sub);
    }



    public void visit(NegativeStartExpr negativeStartExpr) {
        Code.put(Code.neg);
    }

    public void visit(MethodDecl methorDecl) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    public void visit(MethodName methodName) {
        if ("main".equals(methodName.getName())) {
        	mainPc = Code.pc;
        }
        int formalParamCnt = methodName.obj.getLevel();
        int localCnt = methodName.obj.getLocalSymbols().size();

        methodName.obj.setAdr(Code.pc);
        
        Code.put(Code.enter);
        Code.put(formalParamCnt);
        Code.put(localCnt);
    }

    


    
    
    
   public void visit(NumberConst numberConst) {
        Obj con = Tab.insert(Obj.Con, "$", numberConst.struct);
        con.setAdr(numberConst.getN1());
        Code.load(con);
    }
    
    public void visit(CharacterConst charConst) {
        Obj con = Tab.insert(Obj.Con, "$", charConst.struct);
        con.setAdr(charConst.getC1());
        Code.load(con);
    }
    
    public void visit(BooleanConst boolConst) {
        Obj con = Tab.insert(Obj.Con, "$", boolConst.struct);
        con.setAdr(boolConst.getB1().equals("true") ? 1 : 0);
        Code.load(con);
    }
    
    public void visit(ConstDef constDef) {
        String name = constDef.getConstName();
        String fullName = !Objects.equals(currNamespace, "") ? currNamespace + "$" + name : name;
        constDef.obj = Tab2.findSymbol2(fullName);
        SyntaxNode constNode = constDef.getConst();
        int value = -1;

        if (constNode instanceof NumConst) {
            value = ((NumConst) constNode).getN1();
        } else if (constNode instanceof CharConst) {
            value = ((CharConst) constNode).getC1();
        } else if (constNode instanceof BoolConst) {
            value = ((BoolConst) constNode).getB1().equals("true") ? 1 : 0;
        }

        constDef.obj.setAdr(value);
        Code.load(constDef.obj);
    }
    
    
    
    public void visit(DesignatorExpr designatorExpr) {
        Obj designatorObj = designatorExpr.getDesignator().obj;

        if (designatorObj.getKind() == Obj.Elem) {
            Code.put(designatorObj.getType() == Tab.charType ? Code.bastore : Code.astore);
        } else {
            Code.store(designatorObj);
        }
    }
    
    
    public void visit(WithoutNamespace withoutNamespace) {
    	Obj obj = Tab.currentScope().findSymbol(withoutNamespace.obj.getName());

        if (obj != null && obj.getKind() == Obj.Meth) {
            Code.loadConst(0);
            return;
        }
        if (withoutNamespace.obj.getType().getKind() == 3 && ((Designator) withoutNamespace.getParent()).getOptBraExpr() instanceof NoArrayExpression)
            return;
        Code.load(withoutNamespace.obj);
    }

    
    public void visit(WithNamespace withNamespace) {
        if (withNamespace.obj.getKind() == Obj.Meth ||
        	(withNamespace.obj.getType().getKind() == 3 && ((Designator) withNamespace.getParent()).getOptBraExpr() instanceof NoArrayExpression))
            return;
        Code.load(withNamespace.obj);
    }
    
    
    public void visit(PrintExpr printExpr) {
        if (printExpr.getExpr().struct == Tab.intType || printExpr.getExpr().struct == Tab2.boolType) {
            Code.loadConst(5);
            Code.put(Code.print);
        } else {
            Code.loadConst(1);
            Code.put(Code.bprint);
        }
    }
    
    public void visit(ReadDesignator readDesignator) {
        Code.put(Code.read);
        Code.store(readDesignator.getDesignator().obj);
    }
    
    public void visit(DesignatorPP designatorPP) {
        Code.put(Code.const_1);
        Code.put(Code.add);
        Code.store(designatorPP.getDesignator().obj);
    }


    public void visit(DesignatorMM designatorMM) {
        Code.put(Code.const_1);
        Code.put(Code.sub);
        Code.store(designatorMM.getDesignator().obj);
    }

    

    public void visit(NewType newType) {
        Code.put(Code.newarray);
        if (newType.getType().struct == Tab.charType)
            Code.put(0);
        else
            Code.put(1);
    }
    
    public void visit(AddArrayExpression addArrayExpression) {
        Obj obj = addArrayExpression.getParent() instanceof Designator ?
                ((Designator) addArrayExpression.getParent()).obj :
                ((AddArrayExpression) addArrayExpression.getParent()).obj;
    }
    
    public void visit(BraExpr braExpr) {
        if (braExpr.getParent().getParent() instanceof Designator) {
            SyntaxNode parent = braExpr.getParent().getParent().getParent();
            if (!(parent instanceof DesignatorExpr)
                    && !(parent instanceof ReadDesignator)
            )
            	Code.put(braExpr.obj.getType() == Tab.charType ? Code.baload : Code.aload);
        } else {
        	Code.put(braExpr.obj.getType() == Tab.charType ? Code.baload : Code.aload);
        }
    }
    
    

    
  
    
}