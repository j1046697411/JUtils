package org.jzl.utils.chain;

/**
 * <pre>
 *     @author : jzl
 *     time     : 2018/11/01
 *     desc     : xxxx
 *     @since  : 1.0
 * </pre>
 */
public interface Chain<RES, REQ> {

    void addInterceptor(Chain.Interceptor<RES, REQ> interceptor);

    RES proceed(REQ request);

    interface Interceptor<RES, REQ> {
        RES intercept(Chain<RES, REQ> chain, REQ request);
    }

    abstract class AbstractChain<RES, REQ> implements Chain<RES, REQ>, Chain.Interceptor<RES, REQ> {

        private AbstractChain.InterceptorNode<RES, REQ> mRootInterceptor;

        protected AbstractChain() {
            this.mRootInterceptor = new AbstractChain.InterceptorNode<>(this);
        }

        //添加到头部
        @Override
        public final void addInterceptor(Chain.Interceptor<RES, REQ> interceptor) {
            addHeadInterceptor(interceptor);
        }

        public final void addHeadInterceptor(Chain.Interceptor<RES, REQ> interceptor) {
            if (interceptor instanceof AbstractChain) {
                this.mRootInterceptor = mRootInterceptor.addAndGetHeadInterceptor(((AbstractChain<RES, REQ>) interceptor).mRootInterceptor);
                return;
            }
            this.mRootInterceptor = mRootInterceptor.addAndGetHeadInterceptor(interceptor);
        }

        public final void addFootInterceptor(Chain.Interceptor<RES, REQ> interceptor) {
            if (interceptor instanceof AbstractChain) {
                mRootInterceptor.addAndGetFootInterceptor(((AbstractChain) interceptor).mRootInterceptor);
            } else {
                mRootInterceptor.addAndGetFootInterceptor(interceptor);
            }
        }

        @Override
        public final RES proceed(REQ request) {
            return mRootInterceptor.intercept(mRootInterceptor, request);
        }

        @Override
        public final RES intercept(Chain<RES, REQ> chain, REQ request) {
            return lastIntercept(chain, request);
        }

        /**
         * 该链最后一次连接，用于处理没有被拦截的返回值
         *
         * @param chain   链
         * @param request 请求
         * @return 响应的结果
         */
        protected abstract RES lastIntercept(Chain<RES, REQ> chain, REQ request);


        private static class InterceptorNode<RES, REQ> implements Chain<RES, REQ>, Chain.Interceptor<RES, REQ> {

            private AbstractChain.InterceptorNode<RES, REQ> mNextInterceptor;
            private Chain.Interceptor<RES, REQ> mCurrentInterceptor;

            private InterceptorNode(Chain.Interceptor<RES, REQ> interceptor) {
                this.mCurrentInterceptor = interceptor;
            }

            private InterceptorNode(Chain.Interceptor<RES, REQ> interceptor, AbstractChain.InterceptorNode<RES, REQ> nextInterceptor) {
                this.mCurrentInterceptor = interceptor;
                this.mNextInterceptor = nextInterceptor;
            }

            @Override
            public void addInterceptor(Chain.Interceptor<RES, REQ> interceptor) {
                addAndGetFootInterceptor(interceptor);
            }

            public AbstractChain.InterceptorNode<RES, REQ> addAndGetFootInterceptor(Chain.Interceptor<RES, REQ> interceptor) {
                if (hasNextInterceptor()) {
                    return mNextInterceptor.addAndGetFootInterceptor(interceptor);
                } else {
                    if (interceptor instanceof AbstractChain.InterceptorNode) {
                        this.mNextInterceptor = (AbstractChain.InterceptorNode<RES, REQ>) interceptor;
                    } else {
                        this.mNextInterceptor = new AbstractChain.InterceptorNode<>(interceptor);
                    }
                    return mNextInterceptor;
                }
            }

            public AbstractChain.InterceptorNode<RES, REQ> addAndGetHeadInterceptor(Chain.Interceptor<RES, REQ> interceptor) {
                if (interceptor instanceof AbstractChain.InterceptorNode) {
                    ((AbstractChain.InterceptorNode<RES, REQ>) interceptor).addAndGetFootInterceptor(this);
                    return (AbstractChain.InterceptorNode<RES, REQ>) interceptor;
                } else {
                    return new AbstractChain.InterceptorNode<>(interceptor, this);
                }
            }

            @Override
            public RES proceed(REQ request) {
                if (hasNextInterceptor()) {
                    return mNextInterceptor.intercept(mNextInterceptor, request);
                }
                throw new RuntimeException("$^ _ ^$");
            }

            protected boolean hasNextInterceptor() {
                return mNextInterceptor != null;
            }

            @Override
            public RES intercept(Chain<RES, REQ> chain, REQ request) {
                return mCurrentInterceptor.intercept(chain, request);
            }
        }
    }
}
