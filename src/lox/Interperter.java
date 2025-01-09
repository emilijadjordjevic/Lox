package lox;

// import javax.management.RuntimeErrorException;

import lox.Expr.Binary;
import lox.Expr.Grouping;
import lox.Expr.Literal;
import lox.Expr.Unary;

public class Interperter implements Expr.Visitor<Object> {

    class RuntimeError extends RuntimeException {
        final Token token;

        RuntimeError(Token token, String message) {
            super(message);
            this.token = token;
        }
    }

    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError re) {
            System.out.println(re.getMessage());
        }
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left,  right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) 
                    return (double)left + (double)right;

                if(left instanceof String && right instanceof String) 
                    return (String)left + (String)right;
                throw new RuntimeError(expr.operator, "Operans must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left,  right);
                validateDivision(expr.operator, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left,  right);
                return (double)left * (double)right;
            case GREATER:
                checkNumberOperands(expr.operator, left,  right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left,  right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left,  right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left,  right);
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
            default:
                break;
        }

        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;

            case BANG:
                return !isTruthy(right);
            default:
                break;
        }

        return null;
    }

    private boolean isTruthy(Object o) {
        if (o == null) return false;
        if (o instanceof Boolean) return (boolean)o;

        return true;
    }

    private boolean isEqual(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null) return false;

        return o1.equals(o2);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number. ");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be numbers");
    }

    private void validateDivision(Token operator, Object operand) {
        if ((double)operand != 0) return;

        throw new RuntimeError(operator, "Division by zero.");
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();

            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    } 
    
}
