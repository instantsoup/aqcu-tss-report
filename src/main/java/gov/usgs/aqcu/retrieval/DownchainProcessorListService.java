package gov.usgs.aqcu.retrieval;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aquaticinformatics.aquarius.sdk.timeseries.AquariusClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.DownchainProcessorListByTimeSeriesServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ProcessorListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Processor;

import net.servicestack.client.IReturn;
import net.servicestack.client.WebServiceException;

@Component
public class DownchainProcessorListService extends AquariusRetrievalService {
	private static final Logger LOG = LoggerFactory.getLogger(UpchainProcessorListService.class);

	public ProcessorListServiceResponse getRawResponse(String primaryTimeseriesIdentifier, Instant startDate, Instant endDate) throws Exception {
				DownchainProcessorListByTimeSeriesServiceRequest request = new DownchainProcessorListByTimeSeriesServiceRequest()
				.setTimeSeriesUniqueId(primaryTimeseriesIdentifier)
				.setQueryFrom(startDate)
				.setQueryTo(endDate);
		ProcessorListServiceResponse processorsResponse = executePublishApiRequest(request);
		return processorsResponse;
	}
	
	public List<Processor> getProcessorList(String primaryTimeseriesIdentifier, Instant startDate, Instant endDate) throws Exception {
		return getRawResponse(primaryTimeseriesIdentifier, startDate, endDate).getProcessors();
	}
	
	public List<String> getOutputTimeSeriesUniqueIdList(ProcessorListServiceResponse response) {
		Set<String> uniqueIds = new HashSet<>();
		
		for(Processor proc : response.getProcessors()) {
			uniqueIds.add(proc.getOutputTimeSeriesUniqueId());
		}
		
		return new ArrayList<>(uniqueIds);
	}
	
	public List<String> getOutputTimeSeriesUniqueIdList(String primaryTimeseriesIdentifier, Instant startDate, Instant endDate) throws Exception {
		return getOutputTimeSeriesUniqueIdList(getRawResponse(primaryTimeseriesIdentifier, startDate, endDate));
	}
}
