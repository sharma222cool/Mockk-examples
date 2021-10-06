package com.learning.mockingsamples;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class RxSchedulersOverrideRule implements TestRule
{
    private final Function<Scheduler, Scheduler> rxJavaTrampolineScheduler = scheduler -> Schedulers.trampoline();

    @Override
    public Statement apply(final Statement base, Description description)
    {
        return new Statement()
        {
            @Override
            public void evaluate() throws Throwable
            {
                RxJavaPlugins.setIoSchedulerHandler(rxJavaTrampolineScheduler);
                RxJavaPlugins.setComputationSchedulerHandler(rxJavaTrampolineScheduler);
                RxJavaPlugins.setNewThreadSchedulerHandler(rxJavaTrampolineScheduler);
                RxAndroidPlugins.setInitMainThreadSchedulerHandler(callable -> Schedulers.trampoline());
                RxAndroidPlugins.setMainThreadSchedulerHandler(rxJavaTrampolineScheduler);

                base.evaluate();
                RxJavaPlugins.reset();
                RxAndroidPlugins.reset();
            }
        };
    }
}