package com.thorstenmarx.webtools.modules.ecommerce.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author marx
 */
public class AsyncProfileGenerator {

	private final ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
	
	public AsyncProfileGenerator () {
		
	}
	
	public void generate (final ProfileGenerator... profiles) {
		forkJoinPool.invoke(new ProfileAction(Arrays.asList(profiles)));
	}
	
	private static class ProfileAction extends RecursiveAction {

		private final List<ProfileGenerator> profiles;

		private ProfileAction(final List<ProfileGenerator> profiles) {
			this.profiles = profiles;
		}

		@Override
		protected void compute() {
			List<ForkJoinTask> tasks = new ArrayList<>();
			
			profiles.stream().map((profile) -> {
				return new RecursiveAction() {
					@Override
					protected void compute() {
						profile.generate();
					}
				};
			}).forEach(tasks::add);
			
			ForkJoinTask.invokeAll(tasks);
		}
	}
}
