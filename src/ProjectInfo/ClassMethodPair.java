package ProjectInfo;

public class ClassMethodPair <C,M> {

    private final C psiClass;
    private final M psiMethod;

    public ClassMethodPair(C left, M right) {
        assert left != null;
        assert right != null;

        this.psiClass = left;
        this.psiMethod = right;
    }

    public C getLeft() {
        return psiClass;
    }
    public M getRight() {
        return psiMethod;
    }

    @Override
    public int hashCode() { return psiClass.hashCode() ^ psiMethod.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ClassMethodPair)) return false;
        ClassMethodPair pairo = (ClassMethodPair) o;
        return this.psiClass.equals(pairo.getLeft()) &&
                this.psiMethod.equals(pairo.getRight());
    }
    @Override
    public String toString(){
        return psiClass+"#"+psiMethod;
    }
}
