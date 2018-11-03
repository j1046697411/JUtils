package org.jzl.fun;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/10/13
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public interface IPredicate<T> {
    boolean test(T target);

    interface ICharPredicate{
        boolean test(char c);
    }
}
