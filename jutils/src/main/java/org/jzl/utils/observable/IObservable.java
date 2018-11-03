package org.jzl.utils.observable;

import org.jzl.fun.IDisposable;
import org.jzl.utils.ObjectUtil;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * author jzl
 * time 2018/8/20
 */
public interface IObservable<T extends IObservable.IObserver> extends IDisposable {
    void register(T observer);

    void unregister(T observer);

    interface IObserver {
    }

    abstract class AbstractObservable<T extends IObservable.IObserver> implements IObservable<T>, IDisposable {

        private Collection<T> mObservers;

        protected AbstractObservable(Collection<T> mObservers) {
            this.mObservers = ObjectUtil.requireNonNull(mObservers);
        }

        protected AbstractObservable() {
            this(new CopyOnWriteArrayList<T>());
        }

        protected Collection<T> getObservers() {
            return mObservers;
        }

        @Override
        public void register(T observer) {
            if (ObjectUtil.nonNull(observer)) {
                mObservers.add(observer);
            }
        }

        @Override
        public void unregister(T observer) {
            if (ObjectUtil.nonNull(observer)) {
                mObservers.remove(observer);
            }
        }

        @Override
        public void dispose() {
            mObservers.clear();
        }
    }

}
