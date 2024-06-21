package com.learning.googleads.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.ads.googleads.v17.resources.Asset;
import com.google.ads.googleads.v17.resources.AssetGroup;
import com.google.ads.googleads.v17.resources.AssetGroupAsset;
import com.google.ads.googleads.v17.resources.BiddingStrategy;
import com.google.ads.googleads.v17.resources.Campaign;
import com.google.ads.googleads.v17.resources.CampaignBudget;
import com.google.ads.googleads.v17.resources.CampaignCriterion;
import com.google.ads.googleads.v17.services.AssetGroupAssetOperation;
import com.google.ads.googleads.v17.services.AssetGroupOperation;
import com.google.ads.googleads.v17.services.AssetOperation;
import com.google.ads.googleads.v17.services.CampaignBudgetOperation;
import com.google.ads.googleads.v17.services.CampaignCriterionOperation;
import com.google.ads.googleads.v17.services.CampaignOperation;
import com.google.ads.googleads.v17.services.GoogleAdsRow;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v17.services.MutateGoogleAdsResponse;
import com.google.ads.googleads.v17.services.GoogleAdsServiceClient.SearchPagedResponse;
import com.google.ads.googleads.v17.services.MutateOperation;
import com.google.ads.googleads.v17.services.MutateOperationResponse;
import com.google.ads.googleads.v17.utils.ResourceNames;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v17.common.ImageAsset;
import com.google.ads.googleads.v17.common.LanguageInfo;
import com.google.ads.googleads.v17.common.LocationInfo;
import com.google.ads.googleads.v17.common.MaximizeConversionValue;
import com.google.ads.googleads.v17.common.Metrics;
import com.google.ads.googleads.v17.common.TextAsset;
import com.google.ads.googleads.v17.enums.AdvertisingChannelTypeEnum.AdvertisingChannelType;
import com.google.ads.googleads.v17.enums.AssetFieldTypeEnum.AssetFieldType;
import com.google.ads.googleads.v17.enums.AssetGroupStatusEnum.AssetGroupStatus;
import com.google.ads.googleads.v17.enums.BudgetDeliveryMethodEnum.BudgetDeliveryMethod;
import com.google.ads.googleads.v17.enums.CampaignStatusEnum.CampaignStatus;
import com.learning.googleads.api.auth.GoogleAdsClientFactory;
import com.learning.googleads.api.web.CampaignDetailsDTO;
import com.learning.googleads.api.web.CampaignMetricsDTO;
import com.learning.googleads.api.web.SearchGoogleAdsRequestFactory;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    private static final int BUDGET_TEMPORARY_ID = -1;
    private static final int PERFORMANCE_MAX_CAMPAIGN_TEMPORARY_ID = -2;
    private static final int ASSET_GROUP_TEMPORARY_ID = -3;
    private static long temporaryId = ASSET_GROUP_TEMPORARY_ID - 1;

    private final SearchGoogleAdsRequestFactory searchGoogleAdsRequestFactory;
    private final GoogleAdsClientFactory googleAdsClientFactory;

    private static final Logger logger = LoggerFactory.getLogger(CampaignService.class);

    @Autowired
    public CampaignService(GoogleAdsClientFactory googleAdsClientFactory,
            SearchGoogleAdsRequestFactory searchGoogleAdsRequestFactory) {
        this.searchGoogleAdsRequestFactory = searchGoogleAdsRequestFactory;
        this.googleAdsClientFactory = googleAdsClientFactory;
    }

    public ResponseEntity<List<Long>> getAllCampaignIds(String customerId) throws Exception {
        try {
            String query = "SELECT campaign.id FROM campaign";
            SearchPagedResponse response = searchGoogleAdsRequestFactory.createCampaignQuery(customerId, query);
            List<Long> campaignIds = new ArrayList<Long>();
            for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                Long campaign = googleAdsRow.getCampaign().getId();
                campaignIds.add(campaign);
            }
            logger.debug("campaignIds: " + campaignIds.toString());
            return ResponseEntity.ok(campaignIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public CampaignDetailsDTO getCampaignDetails(String customerId, String campaignId) throws Exception {
        logger.debug("getCampaignDetails called");
        try {
            String query = "SELECT campaign.id, campaign.keyword_match_type, campaign.name, " +
                    "campaign.optimization_score, campaign.primary_status, campaign.primary_status_reasons, " +
                    "campaign.serving_status, campaign.start_date, campaign.status, " +
                    "campaign.tracking_setting.tracking_url, bidding_strategy.type FROM campaign " +
                    "WHERE customer.id = " + customerId + " AND campaign.id = " + campaignId;

            logger.debug(query);

            SearchPagedResponse response = searchGoogleAdsRequestFactory.createCampaignQuery(customerId, query);
            logger.debug("SearchPagedResponse is valid");
            for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                Campaign campaign = googleAdsRow.getCampaign();
                BiddingStrategy biddingStrategy = googleAdsRow.getBiddingStrategy();

                CampaignDetailsDTO campaignDetailsDTO = new CampaignDetailsDTO();
                campaignDetailsDTO.setCustomerId(Long.parseLong(customerId));
                campaignDetailsDTO.setCampaignId(campaign.getId());
                campaignDetailsDTO.setKeywordMatchType(campaign.getKeywordMatchType());
                campaignDetailsDTO.setCampaignName(campaign.getName());
                campaignDetailsDTO.setOptimizationScore(campaign.getOptimizationScore());
                campaignDetailsDTO.setPrimaryStatus(campaign.getPrimaryStatus());
                campaignDetailsDTO.setPrimaryStatusReasons(campaign.getPrimaryStatusReasonsList());
                campaignDetailsDTO.setServingStatus(campaign.getServingStatus());
                campaignDetailsDTO.setStartDate(campaign.getStartDate());
                campaignDetailsDTO.setCampaignStatus(campaign.getStatus());
                campaignDetailsDTO.setTrackingUrl(campaign.getTrackingSetting().getTrackingUrl());
                campaignDetailsDTO.setBiddingStrategyType(biddingStrategy.getType());

                logger.debug("CampaignDetailsDTO: {}", campaignDetailsDTO);
                return campaignDetailsDTO;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public CampaignMetricsDTO getCampaignMetrics(String customerId, String campaignId) throws Exception {
        try {
            String query = "SELECT campaign.id, bidding_strategy.type, campaign.name, metrics.all_conversions, " +
                    "metrics.all_conversions_value, metrics.average_cpc, metrics.clicks, metrics.conversions, " +
                    "metrics.conversions_value, metrics.cost_per_conversion, metrics.ctr, metrics.impressions " +
                    "FROM campaign WHERE customer.id = " + customerId + " AND campaign.id = " + campaignId;

            SearchPagedResponse response = searchGoogleAdsRequestFactory.createCampaignQuery(customerId, query);
            for (GoogleAdsRow googleAdsRow : response.iterateAll()) {
                Campaign campaign = googleAdsRow.getCampaign();
                BiddingStrategy biddingStrategy = googleAdsRow.getBiddingStrategy();
                Metrics metrics = googleAdsRow.getMetrics();

                CampaignMetricsDTO campaignMetricsDTO = new CampaignMetricsDTO();
                campaignMetricsDTO.setCampaignId(Long.parseLong(campaignId));
                campaignMetricsDTO.setBiddingStrategyType(biddingStrategy.getType());
                campaignMetricsDTO.setCampaignName(campaign.getName());
                campaignMetricsDTO.setAllConversions(metrics.getAllConversions());
                campaignMetricsDTO.setAllConversionsValue(metrics.getAllConversionsValue());
                campaignMetricsDTO.setAverageCpc(metrics.getAverageCpc());
                campaignMetricsDTO.setClicks(metrics.getClicks());
                campaignMetricsDTO.setConversions(metrics.getConversions());
                campaignMetricsDTO.setConversionsValue(metrics.getConversionsValue());
                campaignMetricsDTO.setCostPerConversion(metrics.getCostPerConversion());
                campaignMetricsDTO.setCtr(metrics.getCtr());
                campaignMetricsDTO.setImpressions(metrics.getImpressions());
                logger.debug("CampaignMetricsDTO: {}", campaignMetricsDTO);

                return campaignMetricsDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public String createPmaxCampaign(String customerId) throws Exception {
        GoogleAdsClient googleAdsClient = googleAdsClientFactory.createGoogleAdsClient();
        List<String> headlines = ImmutableList.of("Travel", "Travel Reviews", "Book travel");
        List<String> headlineAssetResourceNames = createMultipleTextAssets(googleAdsClient, Long.parseLong(customerId),
                headlines);
        // Creates the descriptions.
        List<String> descriptions = ImmutableList.of("Take to the air!", "Fly to the sky!");
        List<String> descriptionAssetResourceNames = createMultipleTextAssets(googleAdsClient,
                Long.parseLong(customerId), descriptions);

        // Retail Pmax
        CampaignBudget campaignBudget = CampaignBudget.newBuilder()
                .setName("Performance Max campaign budget #"
                        + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")))
                // The budget period already defaults to DAILY.
                .setAmountMicros(50_000_000)
                .setDeliveryMethod(BudgetDeliveryMethod.STANDARD)
                // A Performance Max campaign cannot use a shared campaign budget.
                .setExplicitlyShared(false)
                // Set a temporary ID in the budget's resource name, so it can be referenced
                // by the campaign in later steps.
                .setResourceName(ResourceNames.campaignBudget(Long.parseLong(customerId), BUDGET_TEMPORARY_ID))
                .build();
        MutateOperation createCampaignBudget = MutateOperation.newBuilder()
                .setCampaignBudgetOperation(
                        CampaignBudgetOperation.newBuilder().setCreate(campaignBudget).build())
                .build();

        Campaign pmaxCampaign = Campaign.newBuilder()
                .setName("Performance Max campaign #"
                        + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")))
                .setStatus(CampaignStatus.PAUSED)
                .setAdvertisingChannelType(AdvertisingChannelType.PERFORMANCE_MAX)
                .setMaximizeConversionValue(
                        MaximizeConversionValue.newBuilder().setTargetRoas(3.5).build())
                .setUrlExpansionOptOut(false)
                .setResourceName(
                        ResourceNames.campaign(Long.parseLong(customerId), PERFORMANCE_MAX_CAMPAIGN_TEMPORARY_ID))
                .setCampaignBudget(
                        ResourceNames.campaignBudget(Long.parseLong(customerId), BUDGET_TEMPORARY_ID))
                .build();
        MutateOperation createCampaign = MutateOperation.newBuilder()
                .setCampaignOperation(
                        CampaignOperation.newBuilder().setCreate(pmaxCampaign).build())
                .build();
        List<MutateOperation> mutateOperations = new ArrayList<>();
        mutateOperations.add(createCampaignBudget);
        mutateOperations.add(createCampaign);
        mutateOperations.addAll(createCampaignCriterionOperations(Long.parseLong(customerId)));
        String assetGroupResourceName = ResourceNames.assetGroup(Long.parseLong(customerId),
                ASSET_GROUP_TEMPORARY_ID);

        mutateOperations.addAll(
                createAssetGroupOperations(
                        Long.parseLong(customerId),
                        assetGroupResourceName,
                        headlineAssetResourceNames,
                        descriptionAssetResourceNames));
        logger.debug(mutateOperations.toString());
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClientFactory.createGoogleAdsClient()
                .getLatestVersion().createGoogleAdsServiceClient()) {

            MutateGoogleAdsResponse response = googleAdsServiceClient.mutate((customerId), mutateOperations);
            // logger.debug(response.toString());
            return response.toString();
        }

    }

    private List<MutateOperation> createAssetGroupOperations(
            long customerId,
            String assetGroupResourceName,
            List<String> headlineAssetResourceNames,
            List<String> descriptionAssetResourceNames)
            throws IOException {
        List<MutateOperation> mutateOperations = new ArrayList<>();
        String campaignResourceName = ResourceNames.campaign(customerId, PERFORMANCE_MAX_CAMPAIGN_TEMPORARY_ID);
        // Creates the AssetGroup.
        AssetGroup assetGroup = AssetGroup.newBuilder()
                .setName("Performance Max asset group #"
                        + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")))
                .setCampaign(campaignResourceName)
                .addFinalUrls("http://www.example.com")
                .addFinalMobileUrls("http://www.example.com")
                .setStatus(AssetGroupStatus.PAUSED)
                .setResourceName(assetGroupResourceName)
                .build();
        AssetGroupOperation assetGroupOperation = AssetGroupOperation.newBuilder().setCreate(assetGroup).build();
        mutateOperations.add(
                MutateOperation.newBuilder().setAssetGroupOperation(assetGroupOperation).build());

        // For the list of required assets for a Performance Max campaign, see
        // https://developers.google.com/google-ads/api/docs/performance-max/assets

        // An AssetGroup is linked to an Asset by creating a new AssetGroupAsset
        // and providing:
        // the resource name of the AssetGroup
        // the resource name of the Asset
        // the field_type of the Asset in this AssetGroup.

        // To learn more about AssetGroups, see
        // https://developers.google.com/google-ads/api/docs/performance-max/asset-groups

        // Links the previously created multiple text assets.

        // Links the headline assets.
        for (String resourceName : headlineAssetResourceNames) {
            AssetGroupAsset assetGroupAsset = AssetGroupAsset.newBuilder()
                    .setFieldType(AssetFieldType.HEADLINE)
                    .setAssetGroup(assetGroupResourceName)
                    .setAsset(resourceName)
                    .build();
            AssetGroupAssetOperation assetGroupAssetOperation = AssetGroupAssetOperation.newBuilder()
                    .setCreate(assetGroupAsset).build();
            mutateOperations.add(
                    MutateOperation.newBuilder()
                            .setAssetGroupAssetOperation(assetGroupAssetOperation)
                            .build());
        }

        // Links the description assets.
        for (String resourceName : descriptionAssetResourceNames) {
            AssetGroupAsset assetGroupAsset = AssetGroupAsset.newBuilder()
                    .setFieldType(AssetFieldType.DESCRIPTION)
                    .setAssetGroup(assetGroupResourceName)
                    .setAsset(resourceName)
                    .build();
            AssetGroupAssetOperation assetGroupAssetOperation = AssetGroupAssetOperation.newBuilder()
                    .setCreate(assetGroupAsset).build();
            mutateOperations.add(
                    MutateOperation.newBuilder()
                            .setAssetGroupAssetOperation(assetGroupAssetOperation)
                            .build());
        }

        // Creates and links the long headline text asset.
        List<MutateOperation> createAndLinkTextAssetOperations = createAndLinkTextAsset(customerId, "Travel the World",
                AssetFieldType.LONG_HEADLINE);
        mutateOperations.addAll(createAndLinkTextAssetOperations);

        // Creates and links the business name text asset.
        createAndLinkTextAssetOperations = createAndLinkTextAsset(customerId, "Interplanetary Cruises",
                AssetFieldType.BUSINESS_NAME);
        mutateOperations.addAll(createAndLinkTextAssetOperations);

        // Creates and links the image assets.

        // Creates and links the Logo Asset.
        createAndLinkTextAssetOperations = createAndLinkImageAsset(
                customerId, "https://gaagl.page.link/bjYi", AssetFieldType.LOGO, "Marketing Logo");
        mutateOperations.addAll(createAndLinkTextAssetOperations);

        // Creates and links the Marketing Image Asset.
        createAndLinkTextAssetOperations = createAndLinkImageAsset(
                customerId,
                "https://gaagl.page.link/Eit5",
                AssetFieldType.MARKETING_IMAGE,
                "Marketing Image");
        mutateOperations.addAll(createAndLinkTextAssetOperations);

        // Creates and links the Square Marketing Image Asset.
        createAndLinkTextAssetOperations = createAndLinkImageAsset(
                customerId,
                "https://gaagl.page.link/bjYi",
                AssetFieldType.SQUARE_MARKETING_IMAGE,
                "Square Marketing Image");
        mutateOperations.addAll(createAndLinkTextAssetOperations);

        return mutateOperations;
    }

    private List<String> createMultipleTextAssets(
            GoogleAdsClient googleAdsClient, long customerId, List<String> texts) {
        List<MutateOperation> mutateOperations = new ArrayList<>();
        for (String text : texts) {
            Asset asset = Asset.newBuilder().setTextAsset(TextAsset.newBuilder().setText(text)).build();
            AssetOperation assetOperation = AssetOperation.newBuilder().setCreate(asset).build();
            mutateOperations.add(MutateOperation.newBuilder().setAssetOperation(assetOperation).build());
        }

        List<String> assetResourceNames = new ArrayList<>();
        // Creates the service client.
        try (GoogleAdsServiceClient googleAdsServiceClient = googleAdsClient.getLatestVersion()
                .createGoogleAdsServiceClient()) {
            // Sends the operations in a single Mutate request.
            MutateGoogleAdsResponse response = googleAdsServiceClient.mutate(Long.toString(customerId),
                    mutateOperations);
            for (MutateOperationResponse result : response.getMutateOperationResponsesList()) {
                if (result.hasAssetResult()) {
                    assetResourceNames.add(result.getAssetResult().getResourceName());
                }
            }
        }
        return assetResourceNames;
    }

    private List<MutateOperation> createAndLinkImageAsset(
            long customerId, String url, AssetFieldType assetFieldType, String assetName)
            throws IOException {
        List<MutateOperation> mutateOperations = new ArrayList<>();
        String assetResourceName = ResourceNames.asset(customerId, getNextTemporaryId());
        // Creates a media file.
        byte[] assetBytes = ByteStreams.toByteArray(new URL(url).openStream());

        // Creates the Image Asset.
        Asset asset = Asset.newBuilder()
                .setResourceName(assetResourceName)
                .setImageAsset(ImageAsset.newBuilder().setData(ByteString.copyFrom(assetBytes)).build())
                // Provides a unique friendly name to identify your asset. When there is an
                // existing
                // image asset with the same content but a different name, the new name will be
                // dropped
                // silently.
                .setName(assetName)
                .build();
        AssetOperation assetOperation = AssetOperation.newBuilder().setCreate(asset).build();
        mutateOperations.add(MutateOperation.newBuilder().setAssetOperation(assetOperation).build());

        // Creates an AssetGroupAsset to link the Asset to the AssetGroup.
        AssetGroupAsset assetGroupAsset = AssetGroupAsset.newBuilder()
                .setFieldType(assetFieldType)
                .setAssetGroup(ResourceNames.assetGroup(customerId, ASSET_GROUP_TEMPORARY_ID))
                .setAsset(assetResourceName)
                .build();
        AssetGroupAssetOperation assetGroupAssetOperation = AssetGroupAssetOperation.newBuilder()
                .setCreate(assetGroupAsset).build();
        mutateOperations.add(
                MutateOperation.newBuilder().setAssetGroupAssetOperation(assetGroupAssetOperation).build());

        return mutateOperations;
    }

    private List<MutateOperation> createCampaignCriterionOperations(long customerId) {
        String campaignResourceName = ResourceNames.campaign(customerId, PERFORMANCE_MAX_CAMPAIGN_TEMPORARY_ID);
        List<CampaignCriterion> campaignCriteria = new ArrayList<>();
        // Sets the LOCATION campaign criteria.
        // Targets all of New York City except Brooklyn.
        // Location IDs are listed here:
        // https://developers.google.com/google-ads/api/reference/data/geotargets
        // and they can also be retrieved using the GeoTargetConstantService as shown
        // here:
        // https://developers.google.com/google-ads/api/docs/targeting/location-targeting
        //
        // We will add one positive location target for New York City (ID=1023191)
        // and one negative location target for Brooklyn (ID=1022762).
        // First, adds the positive (negative = False) for New York City.
        campaignCriteria.add(
                CampaignCriterion.newBuilder()
                        .setCampaign(campaignResourceName)
                        .setLocation(
                                LocationInfo.newBuilder()
                                        .setGeoTargetConstant(ResourceNames.geoTargetConstant(1023191))
                                        .build())
                        .setNegative(false)
                        .build());
        // Next adds the negative target for Brooklyn.
        campaignCriteria.add(
                CampaignCriterion.newBuilder()
                        .setCampaign(campaignResourceName)
                        .setLocation(
                                LocationInfo.newBuilder()
                                        .setGeoTargetConstant(ResourceNames.geoTargetConstant(1022762))
                                        .build())
                        .setNegative(true)
                        .build());
        // Sets the LANGUAGE campaign criterion.
        campaignCriteria.add(
                CampaignCriterion.newBuilder()
                        .setCampaign(campaignResourceName)
                        // Sets the language.
                        // For a list of all language codes, see:
                        // https://developers.google.com/google-ads/api/reference/data/codes-formats#expandable-7
                        .setLanguage(
                                LanguageInfo.newBuilder()
                                        .setLanguageConstant(ResourceNames.languageConstant(1000)) // English
                                        .build())
                        .build());
        // Returns a list of mutate operations with one operation per criterion.
        return campaignCriteria.stream()
                .map(
                        criterion -> MutateOperation.newBuilder()
                                .setCampaignCriterionOperation(
                                        CampaignCriterionOperation.newBuilder().setCreate(criterion).build())
                                .build())
                .collect(Collectors.toList());
    }

    public List<MutateOperation> createAndLinkTextAsset(
            long customerId, String text, AssetFieldType assetFieldType) {
        List<MutateOperation> mutateOperations = new ArrayList<>();
        String assetResourceName = ResourceNames.asset(customerId, getNextTemporaryId());
        // Creates the Text Asset.
        Asset asset = Asset.newBuilder()
                .setResourceName(assetResourceName)
                .setTextAsset(TextAsset.newBuilder().setText(text).build())
                .build();
        AssetOperation assetOperation = AssetOperation.newBuilder().setCreate(asset).build();
        mutateOperations.add(MutateOperation.newBuilder().setAssetOperation(assetOperation).build());

        // Creates an AssetGroupAsset to link the Asset to the AssetGroup.
        AssetGroupAsset assetGroupAsset = AssetGroupAsset.newBuilder()
                .setFieldType(assetFieldType)
                .setAssetGroup(ResourceNames.assetGroup(customerId, ASSET_GROUP_TEMPORARY_ID))
                .setAsset(assetResourceName)
                .build();
        AssetGroupAssetOperation assetGroupAssetOperation = AssetGroupAssetOperation.newBuilder()
                .setCreate(assetGroupAsset).build();
        mutateOperations.add(
                MutateOperation.newBuilder().setAssetGroupAssetOperation(assetGroupAssetOperation).build());

        return mutateOperations;
    }

    private long getNextTemporaryId() {
        return temporaryId--;
    }
}
