/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.standalone.escapeclause;

import static org.assertj.core.api.Assertions.assertThat;

import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HistoricActivityInstanceEscapeClauseTest extends AbstractEscapeClauseTestCase {

    private String deploymentOneId;

    private String deploymentTwoId;

    @BeforeEach
    protected void setUp() throws Exception {
        deploymentOneId = repositoryService
                .createDeployment()
                .tenantId("One%")
                .addClasspathResource("org/flowable/engine/test/history/HistoricActivityInstanceTest.testHistoricActivityInstanceQuery.bpmn20.xml")
                .deploy()
                .getId();

        deploymentTwoId = repositoryService
                .createDeployment()
                .tenantId("Two_")
                .addClasspathResource("org/flowable/engine/test/history/HistoricActivityInstanceTest.testHistoricActivityInstanceQuery.bpmn20.xml")
                .deploy()
                .getId();

    }

    @AfterEach
    protected void tearDown() throws Exception {
        repositoryService.deleteDeployment(deploymentOneId, true);
        repositoryService.deleteDeployment(deploymentTwoId, true);
    }

    @Test
    public void testQueryByTenantIdLike() {
        runtimeService.startProcessInstanceByKeyAndTenantId("noopProcess", "One%");
        runtimeService.startProcessInstanceByKeyAndTenantId("noopProcess", "Two_");

        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery().activityId("noop").activityTenantIdLike("%|%%");
        assertThat(query.singleResult().getTenantId()).isEqualTo("One%");
        assertThat(query.list()).hasSize(1);
        assertThat(query.count()).isEqualTo(1);

        query = historyService.createHistoricActivityInstanceQuery().activityId("noop").activityTenantIdLike("%|_%");
        assertThat(query.singleResult().getTenantId()).isEqualTo("Two_");
        assertThat(query.list()).hasSize(1);
        assertThat(query.count()).isEqualTo(1);
    }
}