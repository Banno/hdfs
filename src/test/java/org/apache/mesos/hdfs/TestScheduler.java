package org.apache.mesos.hdfs;

import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;
import org.apache.mesos.hdfs.config.SchedulerConf;
import org.apache.mesos.hdfs.state.LiveState;
import org.apache.mesos.hdfs.state.PersistentState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestScheduler {

  private final SchedulerConf schedulerConf = new SchedulerConf(new Configuration());

  @Mock
  SchedulerDriver driver;

  @Mock
  PersistentState persistentState;

  @Captor
  ArgumentCaptor<Collection<Protos.TaskInfo>> taskInfosCapture;

  @Test
  public void acceptsAllTheResourceOffersItCanUntilItHasEnoughToStart() {
    Scheduler scheduler = new Scheduler(schedulerConf, new LiveState(), persistentState);

    scheduler.resourceOffers(driver,
        Lists.newArrayList(
            createTestOffer(0),
            createTestOffer(1),
            createTestOffer(2)
        ));

    verify(driver, times(3)).launchTasks(anyList(), taskInfosCapture.capture());
    assertEquals(3, taskInfosCapture.getValue().size());
  }

  @Test
  public void declinesAnyOffersPastWhatItNeeds() {
    Scheduler scheduler = new Scheduler(schedulerConf, new LiveState(), persistentState);

    scheduler.resourceOffers(driver,
        Lists.newArrayList(
            createTestOffer(0),
            createTestOffer(1),
            createTestOffer(2),
            createTestOffer(3)
        ));

    verify(driver, times(1)).declineOffer(any(Protos.OfferID.class));
  }

  @Before
  public void initializeMocks() {
    MockitoAnnotations.initMocks(this);
  }

  private Protos.OfferID createTestOfferId(int instanceNumber) {
    return Protos.OfferID.newBuilder().setValue("offer" + instanceNumber).build();
  }


  private Protos.Offer createTestOffer(int instanceNumber) {
    return Protos.Offer.newBuilder()
        .setId(createTestOfferId(instanceNumber))
        .setFrameworkId(Protos.FrameworkID.newBuilder().setValue("framework1").build())
        .setSlaveId(Protos.SlaveID.newBuilder().setValue("slave" + instanceNumber).build())
        .setHostname("host" + instanceNumber)
        .build();
  }
}