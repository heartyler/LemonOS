/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.io.ByteArrayDataOutput
 *  com.google.common.io.ByteStreams
 *  io.papermc.paper.event.player.AsyncChatEvent
 *  net.kyori.adventure.text.Component
 *  net.kyori.adventure.text.TextComponent
 *  net.kyori.adventure.text.format.NamedTextColor
 *  net.kyori.adventure.text.format.TextColor
 *  net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
 *  org.bukkit.Axis
 *  org.bukkit.Bukkit
 *  org.bukkit.Color
 *  org.bukkit.GameMode
 *  org.bukkit.GameRule
 *  org.bukkit.Keyed
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Particle
 *  org.bukkit.Particle$DustOptions
 *  org.bukkit.Tag
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.data.Ageable
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.block.data.Directional
 *  org.bukkit.block.data.Orientable
 *  org.bukkit.block.data.Rotatable
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockBurnEvent
 *  org.bukkit.event.block.BlockExplodeEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockSpreadEvent
 *  org.bukkit.event.entity.CreatureSpawnEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.player.PlayerAdvancementDoneEvent
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerCommandSendEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerSwapHandItemsEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.Damageable
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.plugin.messaging.PluginMessageListener
 *  org.bukkit.scheduler.BukkitTask
 *  org.geysermc.cumulus.form.SimpleForm
 *  org.geysermc.cumulus.form.SimpleForm$Builder
 *  org.geysermc.floodgate.api.FloodgateApi
 */
package dev.lemonos;

import dev.lemonos.admin.BackendAdminConfirmClickService;
import dev.lemonos.admin.BackendAdminPagedSelectionClickService;
import dev.lemonos.admin.BackendCubeePageAccessService;
import dev.lemonos.config.BackendConfigBootstrapService;
import dev.lemonos.config.BackendConfigValidationService;
import dev.lemonos.runtime.BackendRuntimeLayout;
import dev.lemonos.storage.BackendYamlStore;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Display;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;

public final class LemonOSPlugin
extends JavaPlugin
implements Listener,
PluginMessageListener {
    private static final String BUNGEE_CHANNEL = "BungeeCord";
    private static final String ADMIN_CHANNEL = BackendAdminProtocol.ADMIN_CHANNEL;
    private static final String SYSTEM_ITEM_CUBEE = "cubee";
    private static final int CLONE_RELOCATIONS = 2;
    private static final String SYSTEM_ITEM_LOGIN = "login";
    private static final String STAYED_CLOSE_DISPLAY = "stayed_close_display";
    private static final String STAYED_CLOSE_HEADER_DISPLAY = "stayed_close_header";
    private static final String STAYED_CLOSE_NAMES_DISPLAY = "stayed_close_names";
    private static final String STAYED_CLOSE_TIMES_DISPLAY = "stayed_close_times";
    private static final String STAYED_CLOSE_TITLE_DISPLAY = "stayed_close_title";
    private static final String STAYED_CLOSE_SUBTITLE_DISPLAY = "stayed_close_subtitle";
    private static final String STAYED_CLOSE_BOTTOM_DISPLAY = "stayed_close_bottom";
    private static final String STAYED_CLOSE_NAME_DISPLAY = "stayed_close_name_";
    private static final String STAYED_CLOSE_TIME_DISPLAY = "stayed_close_time_";
    private static final String STAYED_CLOSE_BEDROCK_BOTTOM_DISPLAY = "stayed_close_bedrock_bottom";
    private static final String BOARD_MADE_ROOM_DISPLAY = "hud_made_room_";
    private static final String BOARD_GREW_HERE_DISPLAY = "hud_grew_here_";
    private static final String BOARD_AUTO_CHAIN_DISPLAY = "hud_auto_chain_";
    private static final List<BackendBoardDefinition> BOARD_DEFINITIONS = List.of(
            new BackendBoardDefinition("made-room", BOARD_MADE_ROOM_DISPLAY, "Made Room", "where ideas stay.", "ideas shaped in Sandbox.", 5.42, -60.86, 15.5, Set.of("lobby", "creative"), true),
            new BackendBoardDefinition("grew-here", BOARD_GREW_HERE_DISPLAY, "Grow", "where good things grow.", "crops planted and harvested.", 5.42, -60.86, -0.5, Set.of("lobby", "survival"), false),
            new BackendBoardDefinition("auto-chain", BOARD_AUTO_CHAIN_DISPLAY, "Good Work", "where useful things come in.", "chop, mine, and plant.", 5.42, -60.86, 7.5, Set.of("lobby", "survival"), false));
    private static final double DISPLAY_BOARD_CLEAR_RADIUS_SQUARED = 16.0;
    private static final double SURVIVAL_CHAIN_PERIOD_MULTIPLIER = 1.18;
    private static final double LOBBY_BORDER_SIZE = 128.0;
    private static final int[] PEOPLE_SLOTS = new int[]{11, 12, 13, 14, 15};
    private static final DateTimeFormatter BACKUP_TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm");
    private static final long BACKUP_TIMEOUT_TICKS = 6000L;
    private static final int CHUNK_STOPPED_CONFIRMATIONS = 3;
    private static final int CHUNK_PROBE_FAILURE_LIMIT = 15;
    private static final int CHUNK_COMPLETION_VERIFY_ATTEMPTS = 20;
    private final NamespacedKey systemItemKey = new NamespacedKey((Plugin)this, "system_item");
    private final NamespacedKey stayedCloseDisplayKey = new NamespacedKey((Plugin)this, "stayed_close_display");
    private final Object hudIoLock = new Object();
    private final Map<UUID, SavedInventory> authInventories = new HashMap<UUID, SavedInventory>();
    private final Map<ServerId, Boolean> serverAvailability = new ConcurrentHashMap<ServerId, Boolean>();
    private final Set<UUID> authLocked = ConcurrentHashMap.newKeySet();
    private final Set<UUID> authenticatedIdentities = ConcurrentHashMap.newKeySet();
    private final Map<UUID, IdentityInput> pendingIdentityInputs = new ConcurrentHashMap<UUID, IdentityInput>();
    private final Map<UUID, String> pendingPasscodes = new ConcurrentHashMap<UUID, String>();
    private final Map<UUID, String> passcodeTitleStatuses = new ConcurrentHashMap<UUID, String>();
    private final Map<UUID, BukkitTask> passcodeStatusTasks = new ConcurrentHashMap<UUID, BukkitTask>();
    private final Map<UUID, BukkitTask> passcodeSuccessTasks = new ConcurrentHashMap<UUID, BukkitTask>();
    private final Set<UUID> passcodeGuidanceShown = ConcurrentHashMap.newKeySet();
    private final Set<UUID> passcodeOverflowWarnings = ConcurrentHashMap.newKeySet();
    private final Map<UUID, BukkitTask> passcodeOverflowTasks = new ConcurrentHashMap<UUID, BukkitTask>();
    private final Map<UUID, ChainState> chainTasks = new HashMap<UUID, ChainState>();
    private final Map<String, UUID> chainReservations = new HashMap<String, UUID>();
    private final Set<UUID> chainBreaks = new HashSet<UUID>();
    private final Set<UUID> pendingSkinInputs = ConcurrentHashMap.newKeySet();
    private final Map<UUID, BukkitTask> skinInputTimeouts = new ConcurrentHashMap<UUID, BukkitTask>();
    private final Set<UUID> activeSkinChanges = ConcurrentHashMap.newKeySet();
    private final Map<UUID, BukkitTask> skinChangeTimeouts = new ConcurrentHashMap<UUID, BukkitTask>();
    private final Map<UUID, PendingSkinApply> pendingSkinApplies = new ConcurrentHashMap<UUID, PendingSkinApply>();
    private final Set<UUID> trustedSkinSynced = ConcurrentHashMap.newKeySet();
    private final Map<UUID, UUID> pendingPrivateNotes = new ConcurrentHashMap<UUID, UUID>();
    private final Set<UUID> defaultLaunchPending = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Integer> peoplePageIndexes = new ConcurrentHashMap<UUID, Integer>();
    private final Map<UUID, Integer> adminPeoplePageIndexes = new ConcurrentHashMap<UUID, Integer>();
    private final Map<UUID, Integer> resetInputCounts = new ConcurrentHashMap<UUID, Integer>();
    private final Map<UUID, Long> bedrockFormTokens = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Long> bedrockActionTokens = new ConcurrentHashMap<UUID, Long>();
    private final AtomicLong bedrockFormTokenCounter = new AtomicLong();
    private final Set<UUID> proxyAdmins = ConcurrentHashMap.newKeySet();
    private final Set<String> knownResetRequests = ConcurrentHashMap.newKeySet();
    private final Map<UUID, UUID> resetSessionIds = new ConcurrentHashMap<>();
    private final UUID resetOwnerInstance = UUID.randomUUID();
    private final BackendOperationRegistry<ServerId, ManualBackupOperation> backupOperations = new BackendOperationRegistry<>();
    private final Set<UUID> pendingActionBarStatuses = ConcurrentHashMap.newKeySet();
    private final BackendOperationRegistry<UUID, AdminSendOperation> adminSendOperations = new BackendOperationRegistry<>();
    private final AtomicLong adminSendGenerationCounter = new AtomicLong();
    private final BackendOperationRegistry<String, ChunkOperation> chunkOperations = new BackendOperationRegistry<>();
    private final AtomicLong chunkGenerationCounter = new AtomicLong();
    private final AtomicLong backupGenerationCounter = new AtomicLong();
    private Object chunkyApi;
    private File identityFile;
    private FileConfiguration identities;
    private File identityLockFile;
    private File placesFile;
    private FileConfiguration places;
    private File skinsFile;
    private FileConfiguration skins;
    private File backupsFile;
    private FileConfiguration backups;
    private File chunksFile;
    private FileConfiguration chunks;
    private File hudDataFile;
    private FileConfiguration hudData;
    private File configFile;
    private FileConfiguration config;
    private File messagesFile;
    private FileConfiguration messages;
    private File sharedPlacesConfigFile;
    private FileConfiguration sharedPlacesConfig;
    private File sandboxFile;
    private FileConfiguration sandbox;
    private File survivalFile;
    private FileConfiguration survival;
    private File boardsFile;
    private FileConfiguration boards;
    private File atmosphereFile;
    private FileConfiguration atmosphere;
    private BackendBoardConfig boardConfig;
    private BackendAtmosphereConfig atmosphereConfig;
    private boolean boardsFileCreated;
    private boolean atmosphereFileCreated;
    private boolean boardsConfigDirty;
    private boolean atmosphereConfigDirty;
    private BackendFeatureConfigMigrationService featureConfigMigrationService;
    private BukkitTask availabilityTask;
    private BukkitTask auraTask;
    private BukkitTask adminCommandTask;
    private BukkitTask careRequestTask;
    private BukkitTask sandboxStatusTask;
    private BukkitTask pendingActionBarStatusTask;
    private BukkitTask actionBarRenderTask;
    private BukkitTask pendingOperationWatchdogTask;
    private BukkitTask chainStatusTask;
    private BukkitTask careWorldStatusTask;
    private BukkitTask restTask;
    private BukkitTask tabTask;
    private BackendBoardLifecycleService boardLifecycleService;
    private BackendAtmosphereLifecycleService atmosphereLifecycleService;
    private BackendBoardOrchestrationService boardOrchestrationService;
    private BackendBoardChunkLeaseService boardChunkLeaseService;
    private BackendWorldPolicy worldPolicy;
    private BackendLobbyBoundsService lobbyBoundsService;
    private BackendFirstJoinSpawnService firstJoinSpawnService;
    private volatile Location lobbyFirstJoinSpawn;
    private BackendAtmosphereOrchestrationService atmosphereOrchestrationService;
    private ZoneId tabTimeZone = ZoneId.of("Asia/Bangkok");
    private DateTimeFormatter tabTimeFormatter = DateTimeFormatter.ofPattern("EEEdd HH:mm", Locale.ENGLISH);
    private long actionBarHeartbeatNanos;
    private final Map<UUID, Long> actionBarErrorLogNanos = new ConcurrentHashMap<UUID, Long>();
    private final Map<String, Long> actionBarProducerErrorLogNanos = new ConcurrentHashMap<String, Long>();
    private final Map<String, Long> operationErrorLogNanos = new ConcurrentHashMap<String, Long>();
    private ServerId currentServer;
    private BackendRuntimeLayout runtimeLayout;
    private BackendYamlStore yamlStore;
    private BackendConfigMigrationService configMigrationService;
    private BackendConfigBootstrapService configBootstrapService;
    private BackendConfigValidationService configValidationService;
    private BackendConfigDefaultGroupService configDefaultGroupService;
    private BackendMainConfigDefaultService mainConfigDefaultService;
    private BackendHudConfigMigrationService hudConfigMigrationService;
    private BackendConfigMigrationOrchestrator configMigrationOrchestrator;
    private BackendSkinProtocolService skinProtocolService;
    private BackendSkinResultService skinResultService;
    private BackendIdentitySkinService identitySkinService;
    private BackendOpenCubeeMessageService openCubeeMessageService;
    private BackendAccessLegacyService accessLegacyService;
    private BackendPluginMessageRouterService pluginMessageRouterService;
    private BackendAdminSendProtocolService adminSendProtocolService;
    private BackendWakePlaceService wakePlaceService;
    private BackendPlaceStatusService placeStatusService;
    private BackendPlaceRuntimeStatusService placeRuntimeStatusService;
    private BackendPlaceAvailabilityService<ServerId> placeAvailabilityService;
    private BackendChunkSettingsService chunkSettingsService;
    private BackendHudDataService hudDataService;
    private BackendHudDisplayService hudDisplayService;
    private BackendDisplayEntityService displayEntityService;
    private BackendDisplayVisibilityService displayVisibilityService;
    private BackendDisplayBoardLifecycleService displayBoardLifecycleService;
    private BackendDisplayPlacementService displayPlacementService;
    private BackendBackupMetadataService backupMetadataService;
    private BackendAccessMetadataService accessMetadataService;
    private BackendOnlineNamesService onlineNamesService;
    private BackendRestStateService restStateService;
    private BackendRestFileService restFileService;
    private BackendStayedClosePlaytimeService stayedClosePlaytimeService;
    private BackendStayedCloseDisplayService stayedCloseDisplayService;
    private BackendAtmosphereWorldService<TimePhase, WeatherPhase> atmosphereWorldService;
    private BackendAtmosphereActionBarService atmosphereActionBarService;
    private BackendAtmosphereMusicService atmosphereMusicService;
    private BackendActivityMessageService activityMessageService;
    private BackendSandboxDrawingSessionService sandboxDrawingSessionService;
    private BackendSandboxHistoryService sandboxHistoryService;
    private BackendSandboxPreviewService sandboxPreviewService;
    private BackendSandboxPreviewLifecycleService sandboxPreviewLifecycleService;
    private BackendSandboxStatusService sandboxStatusService;
    private BackendSandboxGeometryService sandboxGeometryService;
    private BackendSandboxBlockChangeService sandboxBlockChangeService;
    private BackendSandboxDrawingShapeService sandboxDrawingShapeService;
    private BackendSandboxBulkDrawingService sandboxBulkDrawingService;
    private BackendSandboxCenterDrawingService sandboxCenterDrawingService;
    private BackendSandboxTransformPreviewService sandboxTransformPreviewService;
    private BackendSandboxPlacementService sandboxPlacementService;
    private BackendSandboxCloneClearChangeService sandboxCloneClearChangeService;
    private BackendSandboxClonePreviewPlanService sandboxClonePreviewPlanService;
    private BackendSandboxClearPreviewPlanService sandboxClearPreviewPlanService;
    private BackendSandboxCloneClearPreviewStatusService sandboxCloneClearPreviewStatusService;
    private BackendSandboxUndoRedoService sandboxUndoRedoService;
    private BackendSandboxPreviewRenderService sandboxPreviewRenderService;
    private BackendSandboxInputModelService sandboxInputModelService;
    private BackendSandboxMaterialInputService sandboxMaterialInputService;
    private BackendSandboxTransformInputService sandboxTransformInputService;
    private BackendSandboxInputFailureService sandboxInputFailureService;
    private BackendSandboxDrawingTransitionService sandboxDrawingTransitionService;
    private BackendSandboxActiveDrawingLifecycleService sandboxActiveDrawingLifecycleService;
    private BackendCareWorldStatusService careWorldStatusService;
    private BackendTravelConnectService travelConnectService;
    private BackendTravelStateService<ServerId> travelStateService;
    private BackendWakeTravelService<ServerId> wakeTravelService;
    private BackendTravelStartService<ServerId> travelStartService;
    private BackendTravelFinishService<ServerId> travelFinishService;
    private BackendReturnTravelService returnTravelService;
    private BackendLocalTravelService localTravelService;
    private BackendMeetRequestService<RequestKind> meetRequestService;
    private BackendPeopleNavigationService peopleNavigationService;
    private BackendPeopleActionService peopleActionService;
    private BackendAdminPeopleNavigationService adminPeopleNavigationService;
    private BackendAdminPeopleActionService adminPeopleActionService;
    private BackendAdminAccessNavigationService adminAccessNavigationService;
    private BackendAdminAccessActionService adminAccessActionService;
    private BackendAdminResetNavigationService adminResetNavigationService;
    private BackendAdminResetActionService adminResetActionService;
    private BackendAdminPlayerControlService adminPlayerControlService;
    private BackendAdminWorldNavigationService<ChunkDimension> adminWorldNavigationService;
    private BackendAdminChunkActionService adminChunkActionService;
    private BackendBackupOperationService backupOperationService;
    private BackendCubeeNavigationService cubeeNavigationService;
    private BackendCubeeRoutingService cubeeRoutingService;
    private BackendBedrockPageRouteService bedrockPageRouteService;
    private BackendBedrockFallbackService bedrockFallbackService;
    private BackendCubeeItemService cubeeItemService;
    private BackendSandboxInteractionService sandboxInteractionService;
    private BackendPlacesClickService placesClickService;
    private BackendPeopleClickService peopleClickService;
    private BackendRequestsClickService requestsClickService;
    private BackendAdminRootClickService adminRootClickService;
    private BackendAdminKeysClickService adminKeysClickService;
    private BackendAdminPagedSelectionClickService adminPagedSelectionClickService;
    private BackendCubeePageAccessService cubeePageAccessService;
    private BackendAdminKeyHolderClickService adminKeyHolderClickService;
    private BackendAdminConfirmClickService adminConfirmClickService;
    private BackendAdminSelfClickService adminSelfClickService;
    private BackendAdminPeopleClickService adminPeopleClickService;
    private BackendAdminRequestsClickService adminRequestsClickService;
    private BackendAdminResetClickService adminResetClickService;
    private BackendAdminAtmosphereClickService adminAtmosphereClickService;
    private BackendAdminUpkeepClickService adminUpkeepClickService;
    private BackendAdminChunksClickService adminChunksClickService;
    private BackendAdminChunksDimensionClickService<ChunkDimension> adminChunksDimensionClickService;
    private BackendAdminChunksSizeClickService adminChunksSizeClickService;
    private BackendAdminGamemodeClickService adminGamemodeClickService;
    private BackendAdminPlayerClickService adminPlayerClickService;
    private BackendAdminPlayerControlClickService adminPlayerControlClickService;
    private BackendIdentityTransferService identityTransferService;
    private BackendIdentitySessionService identitySessionService;
    private BackendIdentityResetService identityResetService;
    private BackendIdentityAccountService identityAccountService;
    private BackendIdentityPreferenceService identityPreferenceService;
    private BackendIdentityFlowService identityFlowService;
    private BackendIdentityCompletionService identityCompletionService;
    private BackendActionBarCoordinator actionBarCoordinator;
    private BackendPasscodeInputService passcodeInputService;
    private BackendPasscodeLifecycleService passcodeLifecycleService;
    private BackendPasscodeDisplayService passcodeDisplayService;
    public void onEnable() {
        this.getLogger().info("LemonOS build " + this.buildSourceSnapshot());
        this.runtimeLayout = BackendRuntimeLayout.resolve();
        this.yamlStore = new BackendYamlStore();
        this.actionBarCoordinator = new BackendActionBarCoordinator();
        this.configMigrationService = new BackendConfigMigrationService();
        this.configBootstrapService = new BackendConfigBootstrapService(this, this.yamlStore);
        this.configValidationService = new BackendConfigValidationService(this.getLogger());
        this.featureConfigMigrationService = new BackendFeatureConfigMigrationService(this.configMigrationService);
        this.configDefaultGroupService = new BackendConfigDefaultGroupService(this.configMigrationService);
        this.mainConfigDefaultService = new BackendMainConfigDefaultService(this.configMigrationService);
        this.hudConfigMigrationService = new BackendHudConfigMigrationService(this.configMigrationService);
        this.configMigrationOrchestrator = new BackendConfigMigrationOrchestrator(
                this.featureConfigMigrationService, this.configDefaultGroupService, this.getLogger(), this.yamlStore);
        this.skinProtocolService = new BackendSkinProtocolService((Plugin)this, this::finishProxySkinApply);
        this.skinResultService = new BackendSkinResultService(this::saveSkinChoice);
        this.identitySkinService = new BackendIdentitySkinService();
        this.openCubeeMessageService = new BackendOpenCubeeMessageService((Plugin)this, this::handleProxyOpenCubee);
        this.accessLegacyService = new BackendAccessLegacyService((Plugin)this, this::normalizeAccessName, this::updateProxyAdmin);
        this.adminSendProtocolService = new BackendAdminSendProtocolService((Plugin)this, this::finishAdminSendResult);
        this.pluginMessageRouterService = new BackendPluginMessageRouterService(this.openCubeeMessageService, this.skinProtocolService, this.accessLegacyService, this.adminSendProtocolService);
        this.wakePlaceService = new BackendWakePlaceService((Plugin)this);
        this.placeStatusService = new BackendPlaceStatusService();
        this.placeRuntimeStatusService = new BackendPlaceRuntimeStatusService(this.placeStatusService);
        this.placeAvailabilityService = new BackendPlaceAvailabilityService<ServerId>(this.placeRuntimeStatusService);
        this.chunkSettingsService = new BackendChunkSettingsService();
        this.hudDataService = new BackendHudDataService();
        this.hudDisplayService = new BackendHudDisplayService();
        this.displayEntityService = new BackendDisplayEntityService();
        this.displayVisibilityService = new BackendDisplayVisibilityService();
        this.displayBoardLifecycleService = new BackendDisplayBoardLifecycleService();
        this.displayPlacementService = new BackendDisplayPlacementService();
        this.backupMetadataService = new BackendBackupMetadataService();
        this.accessMetadataService = new BackendAccessMetadataService(this::normalizeAccessName, this::safeAdminName);
        this.onlineNamesService = new BackendOnlineNamesService(this::normalizeAccessName, this::safeAdminName);
        this.restStateService = new BackendRestStateService();
        this.restFileService = new BackendRestFileService();
        this.stayedClosePlaytimeService = new BackendStayedClosePlaytimeService();
        this.stayedCloseDisplayService = new BackendStayedCloseDisplayService();
        this.atmosphereWorldService = new BackendAtmosphereWorldService<TimePhase, WeatherPhase>();
        this.atmosphereActionBarService = new BackendAtmosphereActionBarService();
        this.atmosphereMusicService = new BackendAtmosphereMusicService();
        this.activityMessageService = new BackendActivityMessageService();
        this.boardLifecycleService = new BackendBoardLifecycleService((Plugin)this, exception -> this.logFeatureLifecycleFailure("boards", exception));
        this.atmosphereLifecycleService = new BackendAtmosphereLifecycleService((Plugin)this, exception -> this.logFeatureLifecycleFailure("atmosphere", exception));
        this.boardOrchestrationService = new BackendBoardOrchestrationService();
        this.atmosphereOrchestrationService = new BackendAtmosphereOrchestrationService();
        this.sandboxDrawingSessionService = new BackendSandboxDrawingSessionService();
        this.sandboxHistoryService = new BackendSandboxHistoryService();
        this.sandboxPreviewService = new BackendSandboxPreviewService();
        this.sandboxPreviewLifecycleService = new BackendSandboxPreviewLifecycleService();
        this.sandboxStatusService = new BackendSandboxStatusService(this::hasSandboxStatusState);
        this.sandboxGeometryService = new BackendSandboxGeometryService();
        this.sandboxBlockChangeService = new BackendSandboxBlockChangeService();
        this.sandboxDrawingShapeService = new BackendSandboxDrawingShapeService();
        this.sandboxBulkDrawingService = new BackendSandboxBulkDrawingService(this.sandboxDrawingShapeService, this.sandboxBlockChangeService);
        this.sandboxCenterDrawingService = new BackendSandboxCenterDrawingService();
        this.sandboxTransformPreviewService = new BackendSandboxTransformPreviewService();
        this.sandboxPlacementService = new BackendSandboxPlacementService();
        this.sandboxCloneClearChangeService = new BackendSandboxCloneClearChangeService();
        this.sandboxClonePreviewPlanService = new BackendSandboxClonePreviewPlanService();
        this.sandboxClearPreviewPlanService = new BackendSandboxClearPreviewPlanService();
        this.sandboxCloneClearPreviewStatusService = new BackendSandboxCloneClearPreviewStatusService();
        this.sandboxUndoRedoService = new BackendSandboxUndoRedoService();
        this.sandboxPreviewRenderService = new BackendSandboxPreviewRenderService();
        this.sandboxInputModelService = new BackendSandboxInputModelService();
        this.sandboxMaterialInputService = new BackendSandboxMaterialInputService();
        this.sandboxTransformInputService = new BackendSandboxTransformInputService();
        this.sandboxInputFailureService = new BackendSandboxInputFailureService();
        this.sandboxDrawingTransitionService = new BackendSandboxDrawingTransitionService();
        this.sandboxActiveDrawingLifecycleService = new BackendSandboxActiveDrawingLifecycleService();
        this.careWorldStatusService = new BackendCareWorldStatusService();
        this.travelConnectService = new BackendTravelConnectService((Plugin)this, BUNGEE_CHANNEL);
        this.travelStateService = new BackendTravelStateService<ServerId>(this::delayAtmosphereMusicActionBarResume, this.actionBarCoordinator);
        this.travelFinishService = new BackendTravelFinishService<ServerId>(this.travelStateService, this.travelConnectService, this::saveIdentityTransfer, serverId -> serverId.proxyName);
        this.returnTravelService = new BackendReturnTravelService((Plugin)this, this.travelStateService, this::isBusy, this::sameBlockLocation);
        this.localTravelService = new BackendLocalTravelService((Plugin)this, this.travelStateService, this::sameBlockLocation);
        this.meetRequestService = new BackendMeetRequestService<RequestKind>((Plugin)this, this::isBusy, this::isSociallyBusy, kind -> kind == RequestKind.VISIT, this::startLocalTravel, this::startLocalTravel, player -> this.playHomeSound(player, "notification"), this::sendWaitingStatus);
        this.peopleNavigationService = new BackendPeopleNavigationService(this.peoplePageIndexes, this::isSociallyBusy, PEOPLE_SLOTS.length);
        this.peopleActionService = new BackendPeopleActionService(this::isBusy, this::isAuthLocked, this::isSociallyBusy);
        this.adminPeopleNavigationService = new BackendAdminPeopleNavigationService(this.adminPeoplePageIndexes, PEOPLE_SLOTS.length);
        this.adminPeopleActionService = new BackendAdminPeopleActionService(this::isBusy);
        this.adminAccessNavigationService = new BackendAdminAccessNavigationService(this::accessHolderNames, this::networkOnlineNames, this::normalizeAccessName, this::safeAdminName, PEOPLE_SLOTS.length);
        this.adminAccessActionService = new BackendAdminAccessActionService(this::accessHolderNames, () -> this.adminAccessNavigationService.candidateNames(), this::normalizeAccessName, this::safeAdminName);
        this.adminResetNavigationService = new BackendAdminResetNavigationService(this::resetRequestTokens, this::resetRequestName, PEOPLE_SLOTS.length);
        this.adminResetActionService = new BackendAdminResetActionService(this::resetRequestExistsByToken);
        this.adminPlayerControlService = new BackendAdminPlayerControlService(this::isCubee, this::isLoginItem);
        this.adminWorldNavigationService = new BackendAdminWorldNavigationService<ChunkDimension>();
        this.adminChunkActionService = new BackendAdminChunkActionService();
        this.backupOperationService = new BackendBackupOperationService();
        this.cubeeNavigationService = new BackendCubeeNavigationService();
        this.cubeeRoutingService = new BackendCubeeRoutingService();
        this.bedrockPageRouteService = new BackendBedrockPageRouteService();
        this.bedrockFallbackService = new BackendBedrockFallbackService();
        this.cubeeItemService = new BackendCubeeItemService(this::isCubee);
        this.sandboxInteractionService = new BackendSandboxInteractionService();
        this.placesClickService = new BackendPlacesClickService();
        this.peopleClickService = new BackendPeopleClickService();
        this.requestsClickService = new BackendRequestsClickService();
        this.adminRootClickService = new BackendAdminRootClickService();
        this.adminKeysClickService = new BackendAdminKeysClickService();
        this.adminPagedSelectionClickService = new BackendAdminPagedSelectionClickService();
        this.cubeePageAccessService = new BackendCubeePageAccessService();
        this.adminKeyHolderClickService = new BackendAdminKeyHolderClickService();
        this.adminConfirmClickService = new BackendAdminConfirmClickService();
        this.adminSelfClickService = new BackendAdminSelfClickService();
        this.adminPeopleClickService = new BackendAdminPeopleClickService();
        this.adminRequestsClickService = new BackendAdminRequestsClickService();
        this.adminResetClickService = new BackendAdminResetClickService();
        this.adminAtmosphereClickService = new BackendAdminAtmosphereClickService();
        this.adminUpkeepClickService = new BackendAdminUpkeepClickService();
        this.adminChunksClickService = new BackendAdminChunksClickService();
        this.adminChunksDimensionClickService = new BackendAdminChunksDimensionClickService<ChunkDimension>();
        this.adminChunksSizeClickService = new BackendAdminChunksSizeClickService();
        this.adminGamemodeClickService = new BackendAdminGamemodeClickService();
        this.adminPlayerClickService = new BackendAdminPlayerClickService();
        this.adminPlayerControlClickService = new BackendAdminPlayerControlClickService();
        this.wakeTravelService = new BackendWakeTravelService<ServerId>((Plugin)this, this.travelStateService, this::sendWakePlaceRequest, (serverId, status) -> this.setPlaceRuntimeStatus(serverId, status), serverId -> this.canConnect(serverId.port), this::finishTravel);
        this.travelStartService = new BackendTravelStartService<ServerId>((Plugin)this, this.travelStateService, this::isBusy, this::isServerAvailable, this::isPlaceWakeable, this::startWakeTravel, this::finishTravel);
        this.identityTransferService = new BackendIdentityTransferService();
        this.identitySessionService = new BackendIdentitySessionService();
        this.identityResetService = new BackendIdentityResetService();
        this.identityAccountService = new BackendIdentityAccountService();
        this.identityPreferenceService = new BackendIdentityPreferenceService();
        this.identityFlowService = new BackendIdentityFlowService();
        this.identityCompletionService = new BackendIdentityCompletionService();
        BackendPasscodeLayout backendPasscodeLayout = new BackendPasscodeLayout();
        this.passcodeInputService = new BackendPasscodeInputService(backendPasscodeLayout);
        this.passcodeLifecycleService = new BackendPasscodeLifecycleService();
        this.passcodeDisplayService = new BackendPasscodeDisplayService(backendPasscodeLayout);
        this.currentServer = this.detectServer();
        this.worldPolicy = BackendWorldPolicy.forBackend(this.currentServer.proxyName);
        this.lobbyBoundsService = new BackendLobbyBoundsService(LOBBY_BORDER_SIZE);
        this.firstJoinSpawnService = new BackendFirstJoinSpawnService();
        this.boardChunkLeaseService = new BackendBoardChunkLeaseService(this.runtimeLayout.boardChunkState(this.currentServer.proxyName), this.getLogger());
        this.boardChunkLeaseService.recoverOwnedChunks();
        this.boardChunkLeaseService.cleanupLegacyForcedChunksIfRequested();
        this.loadLemonOSConfig();
        this.loadIdentities();
        this.loadPlaces();
        this.loadSkins();
        this.loadBackups();
        this.loadChunks();
        this.loadHudData();
        this.recoverStaleResetRequests();
        this.recoverRuntimeState();
        this.knownResetRequests.addAll(this.resetRequestTokens());
        this.placeAvailabilityService.initialize(this.serverAvailability, List.of(ServerId.values()), this.currentServer);
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, BUNGEE_CHANNEL);
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, ADMIN_CHANNEL);
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, ADMIN_CHANNEL, (PluginMessageListener)this);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        Bukkit.getPluginManager().registerEvents(new BackendWorldProtectionListener(this.worldPolicy), (Plugin)this);
        this.applyWorldRules();
        this.startAvailabilityChecks();
        this.startAuraTask();
        this.startSandboxStatusTask();
        this.startPendingActionBarStatusTask();
        this.startChainStatusTask();
        this.startCareWorldStatusTask();
        this.startAtmosphereTask();
        this.startActionBarRenderTask();
        this.startPendingOperationWatchdog();
        this.startRestTask();
        this.startTabTask();
        this.startBoardTask();
        this.scheduleAdminCommandQueue();
        this.scheduleCareRequestWatcher();
        this.registerChunkyListeners();
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.resetSessionIds.put(player.getUniqueId(), UUID.randomUUID());
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.beginIdentity(player));
        }
    }

    public void onDisable() {
        Player player;
        this.clearOwnedResetRequests();
        if (this.travelStateService != null) {
            this.travelStateService.cancelTasksAndClearStatuses();
        }
        this.meetRequestService.cancelAllAndClear();
        this.adminSendOperations.clear(operation -> this.cleanupAdminSendOperation(operation, false));
        for (BukkitTask bukkitTask : this.skinChangeTimeouts.values()) {
            bukkitTask.cancel();
        }
        for (BukkitTask bukkitTask : this.skinInputTimeouts.values()) {
            bukkitTask.cancel();
        }
        for (BukkitTask bukkitTask : this.sandboxDrawingSessionService.idleTimeouts()) {
            bukkitTask.cancel();
        }
        this.passcodeLifecycleService.cancelTasks(this.passcodeOverflowTasks.values());
        this.passcodeLifecycleService.cancelTasks(this.passcodeStatusTasks.values());
        this.passcodeLifecycleService.cancelTasks(this.passcodeSuccessTasks.values());
        for (ChainState chainState : this.chainTasks.values()) {
            chainState.task.cancel();
        }
        if (this.accessLegacyService != null) {
            this.accessLegacyService.cancelPendingRequests();
        }
        if (this.availabilityTask != null) {
            this.availabilityTask.cancel();
        }
        if (this.adminCommandTask != null) {
            this.adminCommandTask.cancel();
        }
        if (this.careRequestTask != null) {
            this.careRequestTask.cancel();
        }
        if (this.sandboxStatusTask != null) {
            this.sandboxStatusTask.cancel();
        }
        if (this.pendingActionBarStatusTask != null) {
            this.pendingActionBarStatusTask.cancel();
        }
        if (this.actionBarRenderTask != null) {
            this.actionBarRenderTask.cancel();
        }
        if (this.pendingOperationWatchdogTask != null) {
            this.pendingOperationWatchdogTask.cancel();
        }
        this.backupOperations.clear(operation -> this.cleanupBackupOperation(operation, true));
        if (this.auraTask != null) {
            this.auraTask.cancel();
        }
        if (this.chainStatusTask != null) {
            this.chainStatusTask.cancel();
        }
        if (this.careWorldStatusTask != null) {
            this.careWorldStatusTask.cancel();
        }
        if (this.atmosphereLifecycleService != null) this.atmosphereLifecycleService.stop();
        if (this.restTask != null) {
            this.restTask.cancel();
        }
        if (this.tabTask != null) {
            this.tabTask.cancel();
        }
        if (this.boardLifecycleService != null) this.boardLifecycleService.stop();
        if (this.boardChunkLeaseService != null) this.boardChunkLeaseService.releaseAll();
        this.saveHudData();
        this.restoreRestPlaceStatus();
        this.stopAllAtmosphereMusic();
        for (UUID uUID : this.actionBarCoordinator.ids()) {
            player = Bukkit.getPlayer((UUID)uUID);
            if (player == null) continue;
            this.writeActionBar(player, Component.empty());
        }
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            this.restoreAuthInventory(player2);
        }
        if (this.travelStateService != null) {
            this.travelStateService.clear();
        }
        this.sandboxHistoryService.clear();
        this.sandboxDrawingSessionService.clear();
        this.sandboxPreviewService.clearAll();
        this.sandboxStatusService.clear();
        this.careWorldStatusService.clearAll();
        if (this.restStateService != null) {
            this.restStateService.reset();
        }
        this.authInventories.clear();
        this.authLocked.clear();
        this.authenticatedIdentities.clear();
        this.pendingIdentityInputs.clear();
        this.pendingPasscodes.clear();
        this.passcodeTitleStatuses.clear();
        this.passcodeStatusTasks.clear();
        this.passcodeSuccessTasks.clear();
        this.passcodeGuidanceShown.clear();
        this.passcodeOverflowWarnings.clear();
        this.passcodeOverflowTasks.clear();
        this.chainTasks.clear();
        this.chainReservations.clear();
        this.chainBreaks.clear();
        if (this.atmosphereActionBarService != null) {
            this.atmosphereActionBarService.clear();
        }
        if (this.atmosphereMusicService != null) {
            this.atmosphereMusicService.reset();
        }
        if (this.atmosphereWorldService != null) {
            this.atmosphereWorldService.clear();
        }
        this.pendingSkinInputs.clear();
        this.skinInputTimeouts.clear();
        this.activeSkinChanges.clear();
        this.skinChangeTimeouts.clear();
        this.chunkOperations.clear(operation -> { });
        this.pendingActionBarStatuses.clear();
        this.actionBarCoordinator.clear();
        this.actionBarErrorLogNanos.clear();
        this.actionBarProducerErrorLogNanos.clear();
        this.operationErrorLogNanos.clear();
        this.chunkyApi = null;
        this.trustedSkinSynced.clear();
        this.pendingPrivateNotes.clear();
        this.cubeeNavigationService.clear();
        this.defaultLaunchPending.clear();
        this.peoplePageIndexes.clear();
        this.adminPeoplePageIndexes.clear();
        this.bedrockFormTokens.clear();
        this.bedrockActionTokens.clear();
        this.resetInputCounts.clear();
        this.saveChunks();
        if (this.accessLegacyService != null) {
            this.accessLegacyService.clear();
        }
        this.proxyAdmins.clear();
        this.knownResetRequests.clear();
        this.saveIdentities();
        if (this.auraTask != null) {
            this.auraTask.cancel();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.joinMessage(null);
        Player player = playerJoinEvent.getPlayer();
        this.resetSessionIds.put(player.getUniqueId(), UUID.randomUUID());
        this.updateTab(player);
        this.wakeFromRestIfNeeded();
        this.defaultLaunchPending.add(player.getUniqueId());
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.beginIdentity(player));
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.updateStayedCloseDisplayVisibility(player), 20L);
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.updateStayedCloseDisplayVisibility(player), 60L);
        Bukkit.getScheduler().runTaskLater((Plugin)this, this::updateMetricBoards, 65L);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onFirstJoinSpawn(AsyncPlayerSpawnLocationEvent event) {
        if (this.currentServer != ServerId.LOBBY || !event.isNewPlayer()) return;
        Location safeSpawn = this.lobbyFirstJoinSpawn;
        if (safeSpawn != null) event.setSpawnLocation(safeSpawn.clone());
    }

    @EventHandler
    public void onWorldSpawnChange(SpawnChangeEvent event) {
        if (this.currentServer == ServerId.LOBBY && event.getWorld().getName().equals(this.placeSpawnWorld(ServerId.LOBBY))) {
            this.refreshLobbyFirstJoinSpawn(event.getWorld());
        }
    }

    private String buildSourceSnapshot() {
        try (java.io.InputStream inputStream = this.getResource("lemonos-build.properties")) {
            if (inputStream == null) {
                return "unknown";
            }
            java.util.Properties properties = new java.util.Properties();
            properties.load(inputStream);
            return properties.getProperty("sourceSnapshotSha256", "unknown");
        }
        catch (IOException iOException) {
            return "unknown";
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        playerQuitEvent.quitMessage(null);
        this.clearResolvedResetRequest(playerQuitEvent.getPlayer());
        this.cancelTravel(playerQuitEvent.getPlayer(), false);
        this.cancelChain(playerQuitEvent.getPlayer().getUniqueId());
        this.atmosphereActionBarService.remove(playerQuitEvent.getPlayer().getUniqueId());
        this.activityMessageService.remove(playerQuitEvent.getPlayer().getUniqueId());
        this.stopAtmosphereMusic(playerQuitEvent.getPlayer());
        this.clearRequests(playerQuitEvent.getPlayer().getUniqueId());
        this.clearDrawingSession(playerQuitEvent.getPlayer().getUniqueId());
        this.restoreAuthInventory(playerQuitEvent.getPlayer());
        this.trustedSkinSynced.remove(playerQuitEvent.getPlayer().getUniqueId());
        this.clearRuntimeIdentityState(playerQuitEvent.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTask((Plugin)this, this::markRestEmptyIfNeeded);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent playerDeathEvent) {
        playerDeathEvent.deathMessage(null);
        playerDeathEvent.getDrops().removeIf(itemStack -> this.isCubee((ItemStack)itemStack) || this.isLoginItem((ItemStack)itemStack));
        this.cancelTravel(playerDeathEvent.getPlayer(), false);
        this.cancelChain(playerDeathEvent.getPlayer().getUniqueId());
        this.clearDrawingSession(playerDeathEvent.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTask((Plugin)this, () -> {
            if (this.isAuthLocked(playerDeathEvent.getPlayer())) {
                this.hideAuthItems(playerDeathEvent.getPlayer());
            } else {
                this.ensureCubee(playerDeathEvent.getPlayer());
            }
        });
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent playerAdvancementDoneEvent) {
        playerAdvancementDoneEvent.message(null);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onManualChainInterrupt(BlockBreakEvent blockBreakEvent) {
        UUID uUID = blockBreakEvent.getPlayer().getUniqueId();
        if (this.chainBreaks.contains(uUID) || !this.chainTasks.containsKey(uUID)) {
            return;
        }
        this.cancelChain(uUID);
        this.chainBreaks.add(uUID);
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.chainBreaks.remove(uUID));
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onTreeBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        if (!(!this.chainBreaks.contains(player.getUniqueId()) && this.canStartSurvivalChain(player) && this.survivalFeatureEnabled("tree") && this.survivalSneakAllowed(player, "tree") && this.isAxe(player.getInventory().getItemInMainHand()) && this.isLog(blockBreakEvent.getBlock().getType()))) {
            return;
        }
        this.chopConnectedLogs(blockBreakEvent.getBlock(), player);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onVeinMine(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        Block block = blockBreakEvent.getBlock();
        if (!(!this.chainBreaks.contains(player.getUniqueId()) && this.canStartSurvivalChain(player) && this.survivalFeatureEnabled("miner") && this.survivalSneakAllowed(player, "miner") && this.isVeinMineable(block.getType()) && this.canHarvestVeinBlock(player.getInventory().getItemInMainHand(), block))) {
            return;
        }
        this.mineConnectedVein(block, player);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPlantBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        if (this.chainBreaks.contains(player.getUniqueId()) || !this.canStartSurvivalChain(player) || !this.survivalFeatureEnabled("autocrop") || !this.survivalSneakAllowed(player, "autocrop") || !this.isMatureCrop(blockBreakEvent.getBlock())) {
            return;
        }
        this.scheduleCropReplant(blockBreakEvent.getBlock(), player);
        this.harvestConnectedPlants(blockBreakEvent.getBlock(), player);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onAutoPlantBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        if (this.chainBreaks.contains(player.getUniqueId()) || !this.canStartSurvivalChain(player) || !this.survivalFeatureEnabled("auto-plant") || !this.survivalSneakAllowed(player, "auto-plant")) {
            return;
        }
        this.harvestVerticalAutoPlant(blockBreakEvent.getBlock(), player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onActivityBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        if (this.chainBreaks.contains(player.getUniqueId())) {
            return;
        }
        this.recordActivity(player, "break-blocks", 1, this.monotonicMillis());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onActivityBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        this.recordActivity(blockPlaceEvent.getPlayer(), "place-blocks", 1, this.monotonicMillis());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onActivityPickup(EntityPickupItemEvent entityPickupItemEvent) {
        Entity entity = entityPickupItemEvent.getEntity();
        if (entity instanceof Player player) {
            this.recordActivity(player, "pickup-items", Math.max(1, entityPickupItemEvent.getItem().getItemStack().getAmount()), this.monotonicMillis());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onActivityCraft(CraftItemEvent craftItemEvent) {
        HumanEntity humanEntity = craftItemEvent.getWhoClicked();
        if (humanEntity instanceof Player player) {
            ItemStack itemStack = craftItemEvent.getRecipe() == null ? null : craftItemEvent.getRecipe().getResult();
            this.recordActivity(player, "craft-items", itemStack == null ? 1 : Math.max(1, itemStack.getAmount()), this.monotonicMillis());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onSurvivalBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();
        if (!this.canUseSurvivalRefill(player) || !this.survivalBoolean("survival.refill.blocks", true)) {
            return;
        }
        Material material = blockPlaceEvent.getBlockPlaced().getType();
        EquipmentSlot equipmentSlot = blockPlaceEvent.getHand();
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.refillPlacedBlock(player, equipmentSlot, material));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onSurvivalItemBreak(PlayerItemBreakEvent playerItemBreakEvent) {
        Player player = playerItemBreakEvent.getPlayer();
        Material material = playerItemBreakEvent.getBrokenItem().getType();
        if (!this.canUseSurvivalRefill(player) || !this.shouldRefillBrokenItem(material)) {
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.refillMainHand(player, material));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent worldLoadEvent) {
        this.boardChunkLeaseService.recoverWorld(worldLoadEvent.getWorld());
        this.applyWorldRules(worldLoadEvent.getWorld());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent playerCommandPreprocessEvent) {
        Player player = playerCommandPreprocessEvent.getPlayer();
        playerCommandPreprocessEvent.setCancelled(true);
        String string = playerCommandPreprocessEvent.getMessage() == null ? "" : playerCommandPreprocessEvent.getMessage().trim();
        String string2 = this.normalizedCommandName(string.startsWith("/") ? string.substring(1).split("\\s+", 2)[0] : "");
        String string3 = string2;
        if (string2.equals("cubee")) {
            this.handlePadCommand(player);
        } else if (string2.equals("help")) {
            this.handleHelpCommand(player);
        }
    }

    private void handlePadCommand(Player player) {
        this.getLogger().info(player.getName() + " used /cubee");
        if (this.isAuthLocked(player)) {
            this.openLogin(player);
            return;
        }
        if (this.currentServer == ServerId.LOBBY) {
            this.setCubeeVisible(player, true);
            this.ensureCubee(player, false);
            this.openCubee(player);
            return;
        }
        if (this.hasAnyCubeeItem(player)) {
            this.setCubeeVisible(player, false);
            this.purgeCubeeItems(player);
            player.updateInventory();
            this.sendDone(player);
            return;
        }
        if (this.restoreCubeeIfSlotEmpty(player)) {
            this.sendDone(player);
            return;
        }
        this.sendNothingChanged(player);
    }

    private void handleHelpCommand(Player player) {
        this.getLogger().info(player.getName() + " used /help");
        if (this.isAuthLocked(player)) {
            this.openLogin(player);
        } else {
            this.cubeeNavigationService.reset(player.getUniqueId());
            this.defaultLaunchPending.remove(player.getUniqueId());
            this.openCubee(player);
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String string, String[] stringArray) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        String string2 = this.normalizedCommandName(command == null ? string : command.getName());
        if ("cubee".equals(string2)) {
            this.handlePadCommand(player);
            return true;
        }
        if ("help".equals(string2)) {
            this.handleHelpCommand(player);
            return true;
        }
        return true;
    }

    private String normalizedCommandName(String string) {
        String string2 = string == null ? "" : string.trim().toLowerCase(Locale.ROOT);
        int n = string2.lastIndexOf(58);
        return n >= 0 && n + 1 < string2.length() ? string2.substring(n + 1) : string2;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onGrewHereHarvest(BlockBreakEvent blockBreakEvent) {
        if (this.currentServer != ServerId.SURVIVAL || !this.isMatureCrop(blockBreakEvent.getBlock())) {
            return;
        }
        this.recordHudStat("grew-here", blockBreakEvent.getPlayer(), 1);
    }

    private boolean restoreCubeeIfSlotEmpty(Player player) {
        if (!this.cubeeEnabled()) {
            this.setCubeeVisible(player, false);
            return false;
        }
        PlayerInventory playerInventory = player.getInventory();
        int n = this.cubeeSlot();
        ItemStack itemStack = playerInventory.getItem(n);
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            return false;
        }
        this.setCubeeVisible(player, true);
        this.purgeCubeeItems(player);
        playerInventory.setItem(n, this.cubeeItem());
        player.updateInventory();
        return true;
    }

    private boolean hasAnyCubeeItem(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        for (int i = 0; i < playerInventory.getSize(); ++i) {
            if (!this.isCubee(playerInventory.getItem(i))) continue;
            return true;
        }
        return this.isCubee(playerInventory.getItemInOffHand());
    }

    private void sendDone(Player player) {
        player.sendMessage((Component)Component.text((String)"done.", (TextColor)NamedTextColor.GRAY));
    }

    private void sendNothingChanged(Player player) {
        player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
    }

    private void sendWaitingStatus(Player player) {
        this.pendingActionBarStatuses.add(player.getUniqueId());
        this.publishActionBar(player, BackendActionBarCoordinator.Owner.PENDING, Component.text((String)"waiting", (TextColor)NamedTextColor.GRAY));
    }

    private void publishActionBar(Player player, BackendActionBarCoordinator.Owner owner, Component component) {
        if (player == null || !player.isOnline()) return;
        this.actionBarCoordinator.publish(player.getUniqueId(), owner, component);
    }

    private void notifyActionBar(Player player, BackendActionBarCoordinator.Owner owner, Component component, long durationMillis) {
        if (player == null || !player.isOnline()) return;
        this.actionBarCoordinator.notify(player.getUniqueId(), owner, component, System.nanoTime(), durationMillis);
    }

    private void clearActionBar(UUID uuid, BackendActionBarCoordinator.Owner owner) {
        this.actionBarCoordinator.clear(uuid, owner);
    }

    private void startActionBarRenderTask() {
        this.actionBarHeartbeatNanos = System.nanoTime();
        this.actionBarRenderTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> {
            this.actionBarHeartbeatNanos = System.nanoTime();
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    try {
                        BackendActionBarCoordinator.Frame frame = this.actionBarCoordinator.frame(player.getUniqueId(), this.actionBarHeartbeatNanos);
                        if (frame != null) this.writeActionBar(player, frame.component());
                    }
                    catch (RuntimeException exception) {
                        this.logActionBarFailure(player, exception);
                    }
                }
            }
            catch (RuntimeException exception) {
                this.getLogger().warning("LemonOS Action Bar render tick recovered: " + exception.getMessage());
            }
        }, 1L, 1L);
    }

    private void logActionBarFailure(Player player, RuntimeException exception) {
        UUID uuid = player.getUniqueId();
        long now = System.nanoTime();
        long last = this.actionBarErrorLogNanos.getOrDefault(uuid, 0L);
        if (last == 0L || now - last >= 60_000_000_000L) {
            this.actionBarErrorLogNanos.put(uuid, now);
            this.getLogger().warning("LemonOS Action Bar recovered for " + player.getName() + ": " + exception.getMessage());
        }
    }

    private void logFeatureLifecycleFailure(String feature, RuntimeException exception) {
        this.getLogger().warning("LemonOS " + feature + " lifecycle recovered: " + exception.getMessage());
    }

    private void runActionBarProducer(String producer, Runnable action) {
        try {
            action.run();
        }
        catch (RuntimeException exception) {
            long now = System.nanoTime();
            long last = this.actionBarProducerErrorLogNanos.getOrDefault(producer, 0L);
            if (last == 0L || now - last >= 60_000_000_000L) {
                this.actionBarProducerErrorLogNanos.put(producer, now);
                this.getLogger().warning("LemonOS Action Bar producer recovered (" + producer + "): " + exception.getMessage());
            }
        }
    }

    private long monotonicMillis() {
        return System.nanoTime() / 1_000_000L;
    }

    private void writeActionBar(Player player, Component component) {
        player.sendActionBar(component);
    }

    private void startPendingActionBarStatusTask() {
        this.pendingActionBarStatusTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> this.runActionBarProducer("pending", () -> {
            HashSet<UUID> active = new HashSet<UUID>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    if (!this.pendingActionBarStatusActive(player)) continue;
                    UUID uuid = player.getUniqueId();
                    active.add(uuid);
                    this.publishActionBar(player, BackendActionBarCoordinator.Owner.PENDING, Component.text((String)"waiting", (TextColor)NamedTextColor.GRAY));
                }
                catch (RuntimeException exception) {
                    this.logActionBarFailure(player, exception);
                }
            }
            for (UUID uuid : List.copyOf(this.pendingActionBarStatuses)) {
                if (active.contains(uuid)) continue;
                this.pendingActionBarStatuses.remove(uuid);
                this.clearActionBar(uuid, BackendActionBarCoordinator.Owner.PENDING);
            }
            this.pendingActionBarStatuses.addAll(active);
        }), 20L, 20L);
    }

    private boolean pendingActionBarStatusActive(Player player) {
        UUID uuid = player.getUniqueId();
        return this.pendingIdentityInputs.get(uuid) == IdentityInput.RESET_WAITING
                || this.activeSkinChanges.contains(uuid)
                || this.meetRequestService.hasActive(uuid)
                || this.accessLegacyService.hasPendingActor(uuid)
                || this.chunkParticipant(uuid)
                || this.backupParticipant(uuid);
    }

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent playerCommandSendEvent) {
        if (this.hideCommandSuggestions()) {
            playerCommandSendEvent.getCommands().clear();
        }
    }

    private boolean containsCommandChain(String string) {
        return string.contains(";") || string.contains("&&") || string.contains("||") || string.contains("\n") || string.contains("\r");
    }

    private String namespacedAdminCommand(String string, String string2) {
        if (string.equals("chunky")) {
            return string2;
        }
        String[] stringArray = string2.split("\\s+", 2);
        String string3 = stringArray.length > 1 ? " " + stringArray[1] : "";
        return "minecraft:" + string + string3;
    }

    @EventHandler
    public void onChat(AsyncChatEvent asyncChatEvent) {
        if (this.isAuthLocked(asyncChatEvent.getPlayer())) {
            asyncChatEvent.setCancelled(true);
            return;
        }
        if (this.pendingSkinInputs.contains(asyncChatEvent.getPlayer().getUniqueId())) {
            asyncChatEvent.setCancelled(true);
            String string = asyncChatEvent.signedMessage().message().trim();
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.handleSkinInput(asyncChatEvent.getPlayer(), string));
            return;
        }
        if (this.pendingPrivateNotes.containsKey(asyncChatEvent.getPlayer().getUniqueId())) {
            asyncChatEvent.setCancelled(true);
            String string = asyncChatEvent.signedMessage().message().trim();
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.handlePrivateNoteInput(asyncChatEvent.getPlayer(), string));
            return;
        }
        asyncChatEvent.viewers().removeIf(audience -> {
            Player player;
            return audience instanceof Player && this.isAuthLocked(player = (Player)audience);
        });
        asyncChatEvent.renderer((player, component, component2, audience) -> component.color((TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.space()).append(component2.color((TextColor)NamedTextColor.GRAY)));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent playerInteractEvent) {
        if (this.isAuthLocked(playerInteractEvent.getPlayer())) {
            playerInteractEvent.setCancelled(true);
            Action action = playerInteractEvent.getAction();
            if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && this.isLoginItem(playerInteractEvent.getItem())) {
                this.openLogin(playerInteractEvent.getPlayer());
            }
            return;
        }
        Action action = playerInteractEvent.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (this.handleSurvivalChestSort(playerInteractEvent)) {
            return;
        }
        if (this.handleSurvivalToolSwitch(playerInteractEvent)) {
            return;
        }
        if (!this.isCubee(playerInteractEvent.getItem())) {
            this.handleDrawingInteract(playerInteractEvent);
            return;
        }
        playerInteractEvent.setCancelled(true);
        this.openCubee(playerInteractEvent.getPlayer());
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        HumanEntity humanEntity = inventoryClickEvent.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (this.isAuthLocked(player)) {
            inventoryClickEvent.setCancelled(true);
            int n = inventoryClickEvent.getRawSlot();
            InventoryHolder inventoryHolder = inventoryClickEvent.getView().getTopInventory().getHolder();
            if (inventoryHolder instanceof LoginHolder) {
                LoginHolder loginHolder = (LoginHolder)inventoryHolder;
                if (n >= 0 && n < inventoryClickEvent.getView().getTopInventory().getSize()) {
                    this.handleLoginClick(player, loginHolder, n);
                    return;
                }
            }
            if (!this.isLoginItem(inventoryClickEvent.getCurrentItem()) && !this.isLoginItem(inventoryClickEvent.getCursor()) && inventoryClickEvent.getHotbarButton() != this.cubeeSlot()) {
                if (!(inventoryClickEvent.getClickedInventory() instanceof PlayerInventory)) {
                    return;
                }
                if (inventoryClickEvent.getSlot() != this.cubeeSlot()) {
                    return;
                }
            }
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.hideAuthItems(player));
            return;
        }
        InventoryHolder inventoryHolder = inventoryClickEvent.getView().getTopInventory().getHolder();
        if (inventoryHolder instanceof CubeeHolder) {
            CubeeHolder cubeeHolder = (CubeeHolder)inventoryHolder;
            inventoryClickEvent.setCancelled(true);
            int n = inventoryClickEvent.getRawSlot();
            if (n < 0) {
                return;
            }
            if (n >= inventoryClickEvent.getView().getTopInventory().getSize()) {
                return;
            }
            if (this.isEmpty(inventoryClickEvent.getCurrentItem())) {
                return;
            }
            this.playHomeSound(player, "ui-click");
            this.handleCubeeClick(player, cubeeHolder.page, n);
            return;
        }
        boolean bl = this.shouldKeepCubeeItem(player);
        boolean bl2 = bl && inventoryClickEvent.getClickedInventory() instanceof PlayerInventory && inventoryClickEvent.getSlot() == this.cubeeSlot();
        boolean bl3 = bl2;
        if (!(this.isCubee(inventoryClickEvent.getCurrentItem()) || this.isCubee(inventoryClickEvent.getCursor()) || bl && inventoryClickEvent.getHotbarButton() == this.cubeeSlot() || bl2)) {
            return;
        }
        inventoryClickEvent.setCancelled(true);
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.ensureCubee(player));
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onCreativeInventory(InventoryCreativeEvent inventoryCreativeEvent) {
        HumanEntity humanEntity = inventoryCreativeEvent.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (this.currentServer != ServerId.CREATIVE || this.isAuthLocked(player) || !this.cubeeEnabled() || !this.cubeeVisible(player)) {
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.repairCreativeCubeeSlot(player));
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent inventoryDragEvent) {
        HumanEntity humanEntity = inventoryDragEvent.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (this.isAuthLocked(player)) {
            inventoryDragEvent.setCancelled(true);
            return;
        }
        if (inventoryDragEvent.getView().getTopInventory().getHolder() instanceof CubeeHolder) {
            inventoryDragEvent.setCancelled(true);
            return;
        }
        if (this.isCubee(inventoryDragEvent.getOldCursor()) || inventoryDragEvent.getRawSlots().stream().anyMatch(n -> this.isPlayerCubeeRawSlot(inventoryDragEvent, (int)n, player))) {
            inventoryDragEvent.setCancelled(true);
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.ensureCubee(player));
        }
    }

    private boolean isPlayerCubeeRawSlot(InventoryDragEvent inventoryDragEvent, int n, Player player) {
        if (!this.shouldKeepCubeeItem(player)) {
            return false;
        }
        int n2 = inventoryDragEvent.getView().getTopInventory().getSize();
        return n >= n2 && n - n2 == 8;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        HumanEntity humanEntity = inventoryCloseEvent.getPlayer();
        if (!(humanEntity instanceof Player player)) {
            return;
        }
        if (!this.isAuthLocked(player) || this.isBedrockPlayer(player)) {
            return;
        }
        if (inventoryCloseEvent.getInventory().getHolder() instanceof LoginHolder loginHolder && loginHolder.input == IdentityInput.RESET_WAITING) {
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this, () -> {
            if (!player.isOnline() || !this.isAuthLocked(player) || this.isBedrockPlayer(player)) {
                return;
            }
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof LoginHolder) {
                return;
            }
            this.openLogin(player);
        });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent playerDropItemEvent) {
        Player player = playerDropItemEvent.getPlayer();
        if (this.isAuthLocked(playerDropItemEvent.getPlayer())) {
            playerDropItemEvent.setCancelled(true);
            if (this.isLoginItem(playerDropItemEvent.getItemDrop().getItemStack()) || this.isCubee(playerDropItemEvent.getItemDrop().getItemStack())) {
                playerDropItemEvent.getItemDrop().remove();
                Bukkit.getScheduler().runTask((Plugin)this, () -> this.hideAuthItems(player));
            }
            return;
        }
        if (!this.isCubee(playerDropItemEvent.getItemDrop().getItemStack())) {
            return;
        }
        playerDropItemEvent.setCancelled(true);
        playerDropItemEvent.getItemDrop().remove();
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.normalizeCubee(player));
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent playerSwapHandItemsEvent) {
        if (this.isAuthLocked(playerSwapHandItemsEvent.getPlayer())) {
            playerSwapHandItemsEvent.setCancelled(true);
            return;
        }
        if (this.isCubee(playerSwapHandItemsEvent.getMainHandItem()) || this.isCubee(playerSwapHandItemsEvent.getOffHandItem())) {
            playerSwapHandItemsEvent.setCancelled(true);
            this.ensureCubee(playerSwapHandItemsEvent.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onMove(PlayerMoveEvent playerMoveEvent) {
        if (this.isAuthLocked(playerMoveEvent.getPlayer())) {
            Location location = playerMoveEvent.getFrom();
            Location location2 = playerMoveEvent.getTo();
            if (location2 != null && (location.getX() != location2.getX() || location.getY() != location2.getY() || location.getZ() != location2.getZ())) {
                playerMoveEvent.setTo(location);
            }
            return;
        }
        if (this.currentServer == ServerId.LOBBY && playerMoveEvent.getTo() != null) {
            World lobbyWorld = Bukkit.getWorld(this.placeSpawnWorld(ServerId.LOBBY));
            Location lobbySpawn = lobbyWorld == null ? null : lobbyWorld.getSpawnLocation();
            if (this.lobbyBoundsService.appliesTo(playerMoveEvent.getTo(), lobbyWorld)
                    && this.lobbyBoundsService.outside(playerMoveEvent.getTo(), lobbySpawn)) {
                playerMoveEvent.setTo(this.lobbyBoundsService.clamp(playerMoveEvent.getTo(), lobbySpawn));
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onDamage(EntityDamageEvent entityDamageEvent) {
        Player player;
        if (this.worldPolicy.protectFallDamage() && entityDamageEvent.getEntity() instanceof Player && entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL) {
            entityDamageEvent.setCancelled(true);
            return;
        }
        if (this.worldPolicy.protectEnvironment() && entityDamageEvent.getEntity() instanceof Player && this.isEnvironmentDamage(entityDamageEvent.getCause())) {
            entityDamageEvent.setCancelled(true);
            return;
        }
        Entity entity = entityDamageEvent.getEntity();
        if (entity instanceof Player && this.isAuthLocked(player = (Player)entity)) {
            entityDamageEvent.setCancelled(true);
            return;
        }
        entity = entityDamageEvent.getEntity();
        if (entity instanceof Player && this.travelStateService.contains((player = (Player)entity).getUniqueId())) {
            this.cancelTravel(player, true);
        }
        entity = entityDamageEvent.getEntity();
        if (entity instanceof Player) {
            Player damagedPlayer = (Player)entity;
            Bukkit.getScheduler().runTask((Plugin)this, () -> {
                if (damagedPlayer.isOnline() && !damagedPlayer.isDead() && damagedPlayer.getHealth() > 0.0) {
                    this.recordActivity(damagedPlayer, "damage-survived", 1, this.monotonicMillis());
                }
            });
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onDamageByEntity(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Player player;
        Entity entity = entityDamageByEntityEvent.getDamager();
        if (entity instanceof Player && this.chainTasks.containsKey((player = (Player)entity).getUniqueId())) {
            this.cancelChain(player.getUniqueId());
        }
        if (this.worldPolicy.protectPlayerPvp() && entityDamageByEntityEvent.getEntity() instanceof Player && entityDamageByEntityEvent.getDamager() instanceof Player) {
            entityDamageByEntityEvent.setCancelled(true);
        }
    }

    private void beginIdentity(Player player) {
        if (!player.isOnline()) {
            return;
        }
        if (this.isBedrockPlayer(player)) {
            this.openBedrockIdentityBeginAction(player);
            return;
        }
        this.reloadIdentities();
        this.openJavaIdentityBeginAction(player, this.identityFlowService.javaBeginAction(
                this.javaLoginEnabled(),
                () -> this.identityResetGrantExists(this.identityKey(player)),
                () -> this.acceptIdentityTransfer(player),
                () -> this.acceptIdentitySession(player),
                () -> {
                    this.reloadIdentities();
                    return this.identityRegistered(this.identityKey(player));
                }));
    }

    private void openBedrockIdentityBeginAction(Player player) {
        if (this.bedrockTrusted()) {
            this.reloadIdentities();
        }
        BackendIdentityFlowService.BeginAction action = this.identityFlowService.bedrockBeginAction(
                this.bedrockTrusted(),
                () -> this.acceptIdentityTransfer(player));
        switch (action) {
            case KICK_UNAVAILABLE -> player.kick((Component)Component.text((String)this.resultText("not-available-here", "out of range."), (TextColor)NamedTextColor.DARK_GRAY));
            case TRUSTED -> this.finishTrustedIdentity(player, false);
            case TRUSTED_TRANSFERRED -> {
                this.finishTrustedIdentity(player, false);
                player.sendMessage((Component)Component.text((String)this.resultText("youre-here", "you're here."), (TextColor)NamedTextColor.GRAY));
            }
            case FORCE_PASSCODE_RESET, PASSCODE_LOGIN, PASSCODE_CREATE -> {
            }
        }
    }

    private void openJavaIdentityBeginAction(Player player, BackendIdentityFlowService.BeginAction action) {
        switch (action) {
            case TRUSTED -> this.finishTrustedIdentity(player, true);
            case TRUSTED_TRANSFERRED -> {
                this.finishTrustedIdentity(player, true);
                player.sendMessage((Component)Component.text((String)this.resultText("youre-here", "you're here."), (TextColor)NamedTextColor.GRAY));
            }
            case FORCE_PASSCODE_RESET -> this.forceIdentityPasscodeReset(player);
            case PASSCODE_LOGIN -> this.openIdentityPasscodeStart(player, IdentityInput.LOGIN);
            case PASSCODE_CREATE -> this.openIdentityPasscodeStart(player, IdentityInput.CREATE_PASSCODE);
            case KICK_UNAVAILABLE -> {
            }
        }
    }

    private void openIdentityPasscodeStart(Player player, IdentityInput identityInput) {
        this.hideAuthItems(player);
        this.pendingIdentityInputs.put(player.getUniqueId(), identityInput);
        this.authLocked.add(player.getUniqueId());
        this.tellIdentityPrompt(player);
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.openLogin(player), 40L);
    }

    private void openLogin(Player player) {
        if (!player.isOnline() || !this.isAuthLocked(player) || this.isBedrockPlayer(player)) {
            return;
        }
        this.reloadIdentities();
        IdentityInput identityInput = this.pendingIdentityInputs.get(player.getUniqueId());
        String identityKey = this.identityKey(player);
        boolean resetGrantExists = this.identityResetGrantExists(identityKey);
        boolean resetRequestExists = this.resetRequestExistsForIdentity(identityKey);
        identityInput = this.identityInput(this.passcodeInputService.resolveLoginMode(
                this.loginMode(identityInput),
                this.identityRegistered(identityKey) && !resetGrantExists,
                resetRequestExists,
                resetGrantExists));
        this.pendingIdentityInputs.put(player.getUniqueId(), identityInput);
        if (identityInput == IdentityInput.RESET_WAITING) {
            this.openResetWaiting(player);
            return;
        }
        this.openPasscode(player, identityInput == IdentityInput.CREATE_PASSCODE);
    }

    private BackendPasscodeInputService.LoginMode loginMode(IdentityInput identityInput) {
        if (identityInput == null) {
            return null;
        }
        return switch (identityInput) {
            case CREATE_PASSCODE -> BackendPasscodeInputService.LoginMode.CREATE_PASSCODE;
            case LOGIN -> BackendPasscodeInputService.LoginMode.LOGIN;
            case RESET_WAITING -> BackendPasscodeInputService.LoginMode.RESET_WAITING;
        };
    }

    private IdentityInput identityInput(BackendPasscodeInputService.LoginMode loginMode) {
        return switch (loginMode) {
            case CREATE_PASSCODE -> IdentityInput.CREATE_PASSCODE;
            case LOGIN -> IdentityInput.LOGIN;
            case RESET_WAITING -> IdentityInput.RESET_WAITING;
        };
    }

    private void openPasscode(Player player, boolean bl) {
        LoginHolder loginHolder = new LoginHolder(bl ? IdentityInput.CREATE_PASSCODE : IdentityInput.LOGIN);
        String string = this.pendingPasscodes.getOrDefault(player.getUniqueId(), "");
        BackendPasscodeDisplayService.PasscodeDisplay display = this.passcodeDisplayService.display(
                bl,
                string,
                this.passcodeTitleStatuses.get(player.getUniqueId()),
                this.passcodeInputService.enterState(string, false));
        Inventory inventory = Bukkit.createInventory((InventoryHolder)loginHolder, (int)27, (Component)this.loginTitle(display.title()));
        this.setLoginDigits(inventory, display.digits(), Material.BLACK_STAINED_GLASS_PANE);
        if (display.showClear()) {
            this.setButton(inventory, Ui.Login.CLEAR);
        }
        if (display.showReset()) {
            this.setButton(inventory, Ui.Login.RESET);
        }
        this.setLoginEnterButton(inventory, display.enterState(), bl);
        player.openInventory(inventory);
    }

    private Component loginTitle(UUID uUID, boolean bl, String string) {
        return this.loginTitle(this.passcodeDisplayService.title(bl, string, this.passcodeTitleStatuses.get(uUID)));
    }

    private Component loginTitle(BackendPasscodeDisplayService.TitlePlan titlePlan) {
        return switch (titlePlan.kind()) {
            case CREATE_EMPTY -> this.statusTitle(this.promptText("create-passcode", "Create Passcode"), this.promptText("passcode-length", "4-8 numbers"));
            case CREATE_STATUS -> this.statusTitle(this.promptText("create-passcode", "Create Passcode"), titlePlan.status());
            case CREATE_MASKED -> Component.text((String)(this.promptText("create-passcode", "Create Passcode") + " " + ".".repeat(titlePlan.maskLength())), (TextColor)HoneyPalette.DEFAULT_WHITE);
            case LOGIN_EMPTY -> Component.text((String)this.promptText("enter-passcode", "Enter Passcode"), (TextColor)HoneyPalette.DEFAULT_WHITE);
            case LOGIN_STATUS -> this.statusTitle(this.promptText("enter-passcode", "Enter Passcode"), titlePlan.status());
            case LOGIN_MASKED -> Component.text((String)(this.promptText("enter-passcode", "Enter Passcode") + " " + ".".repeat(titlePlan.maskLength())), (TextColor)HoneyPalette.DEFAULT_WHITE);
        };
    }

    private Component subpageTitle(String string, String string2) {
        return Component.text((String)string, (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.text((String)(" / " + string2), (TextColor)NamedTextColor.GRAY));
    }

    private Component statusTitle(String string, String string2) {
        return Component.text((String)string, (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.text((String)(": " + this.titleStatus(string2)), (TextColor)NamedTextColor.GRAY));
    }

    private String titleStatus(String string) {
        return string != null && string.endsWith(".") ? string.substring(0, string.length() - 1) : String.valueOf(string);
    }

    private void setLoginEnterButton(Inventory inventory, String string, boolean overflow, boolean creating) {
        this.setLoginEnterButton(inventory, this.passcodeInputService.enterState(string, overflow), creating);
    }

    private void setLoginEnterButton(Inventory inventory, BackendPasscodeInputService.EnterState enterState, boolean creating) {
        String actionLabel = creating
                ? this.messageText("labels.create", "Create")
                : this.messageText("labels.enter", "Sign in");
        switch (enterState) {
            case TOO_LONG -> inventory.setItem(Ui.Login.ENTER.slot(), this.menuItem(Material.GRAY_STAINED_GLASS_PANE, actionLabel, this.resultText("too-long", "too long.")));
            case HIDDEN -> inventory.setItem(Ui.Login.ENTER.slot(), null);
            case TOO_SHORT -> inventory.setItem(Ui.Login.ENTER.slot(), this.menuItem(Material.GRAY_STAINED_GLASS_PANE, actionLabel, this.resultText("too-short", "too short.")));
            case READY -> inventory.setItem(Ui.Login.ENTER.slot(), this.menuItem(Material.LIME_STAINED_GLASS_PANE, actionLabel, "go in."));
        }
    }

    private void openResetWaiting(Player player) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new LoginHolder(IdentityInput.RESET_WAITING), (int)27, (Component)this.statusTitle("Reset", "waiting."));
        this.setButton(inventory, Ui.Login.CANCEL_RESET);
        player.openInventory(inventory);
    }

    private void setLoginDigits(Inventory inventory) {
        this.setLoginDigits(inventory, this.passcodeDisplayService.digitButtons(), Material.BLACK_STAINED_GLASS_PANE);
    }

    private void setLoginDigits(Inventory inventory, Material material) {
        this.setLoginDigits(inventory, this.passcodeDisplayService.digitButtons(), material);
    }

    private void setLoginDigits(Inventory inventory, List<BackendPasscodeDisplayService.DigitButton> buttons, Material material) {
        for (BackendPasscodeDisplayService.DigitButton button : buttons) {
            inventory.setItem(button.slot(), this.menuItem(material, button.label(), null));
        }
    }

    private Integer loginDigit(int n) {
        return this.passcodeInputService.digit(n);
    }

    private void handleLoginClick(Player player, LoginHolder loginHolder, int n) {
        if (!player.isOnline() || !this.isAuthLocked(player)) {
            return;
        }
        if (this.passcodeSuccessTasks.containsKey(player.getUniqueId())) {
            return;
        }
        if (loginHolder.input == IdentityInput.RESET_WAITING) {
            if (n == Ui.Login.CANCEL_RESET.slot()) {
                this.clearResetRequest(this.identityKey(player));
                this.resetInputCounts.remove(player.getUniqueId());
                this.pendingIdentityInputs.put(player.getUniqueId(), IdentityInput.LOGIN);
                this.pendingPasscodes.remove(player.getUniqueId());
                this.clearPasscodeFeedback(player.getUniqueId());
                this.openPasscode(player, false);
            }
            return;
        }
        Integer n2 = this.loginDigit(n);
        if (n2 != null) {
            this.playHomeSound(player, "numpad-press");
            this.appendPasscodeDigit(player, loginHolder.input, n, n2);
            return;
        }
        if (n == Ui.Login.CLEAR.slot()) {
            this.playHomeSound(player, "ui-click");
            this.pendingPasscodes.remove(player.getUniqueId());
            this.clearPasscodeFeedback(player.getUniqueId());
            this.openPasscode(player, loginHolder.input == IdentityInput.CREATE_PASSCODE);
            return;
        }
        if (n == Ui.Login.RESET.slot() && loginHolder.input == IdentityInput.LOGIN) {
            this.playHomeSound(player, "ui-click");
            this.startPasscodeReset(player);
            return;
        }
        if (n == Ui.Login.ENTER.slot()) {
            String string = this.pendingPasscodes.getOrDefault(player.getUniqueId(), "");
            if (this.passcodeOverflowWarnings.contains(player.getUniqueId()) || string.isEmpty()) {
                return;
            }
            if (string.length() < 4) {
                this.showPasscodeStatus(player, loginHolder.input, "too short.", true);
                return;
            }
            this.playHomeSound(player, "ui-click");
            this.submitPasscode(player, loginHolder.input);
        }
    }

    private void appendPasscodeDigit(Player player, IdentityInput identityInput, int n, int n2) {
        String string = this.pendingPasscodes.getOrDefault(player.getUniqueId(), "");
        if (string.length() >= 8) {
            this.showPasscodeOverflow(player);
            return;
        }
        this.clearPasscodeStatus(player.getUniqueId());
        String string2 = string + n2;
        this.pendingPasscodes.put(player.getUniqueId(), string2);
        if (identityInput == IdentityInput.LOGIN && string2.length() >= 4) {
            this.reloadIdentities();
            if (this.verifyIdentityPasscode(this.identityKey(player), string2)) {
                this.scheduleLoginSuccess(player, string2);
                return;
            }
        }
        this.openPasscode(player, identityInput == IdentityInput.CREATE_PASSCODE);
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getHolder() instanceof LoginHolder) {
            inventory.setItem(n, this.menuItem(Material.LIME_STAINED_GLASS_PANE, String.valueOf(n2), null));
            Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                if (player.isOnline() && !this.passcodeOverflowWarnings.contains(player.getUniqueId()) && player.getOpenInventory().getTopInventory().getHolder() instanceof LoginHolder) {
                    player.getOpenInventory().getTopInventory().setItem(n, this.menuItem(Material.BLACK_STAINED_GLASS_PANE, String.valueOf(n2), null));
                }
            }, 4L);
        }
    }

    private void showPasscodeOverflow(Player player) {
        BukkitTask bukkitTask;
        UUID uUID = player.getUniqueId();
        if (this.passcodeOverflowWarnings.contains(uUID)) {
            return;
        }
        this.playHomeSound(player, "failed");
        this.passcodeOverflowWarnings.add(uUID);
        player.sendMessage((Component)Component.text((String)"too long.", (TextColor)NamedTextColor.DARK_GRAY));
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getHolder() instanceof LoginHolder) {
            boolean bl = ((LoginHolder)inventory.getHolder()).input == IdentityInput.CREATE_PASSCODE;
            this.passcodeTitleStatuses.put(uUID, "too long.");
            this.openPasscode(player, bl);
            inventory = player.getOpenInventory().getTopInventory();
            this.setLoginDigits(inventory, Material.RED_STAINED_GLASS_PANE);
            this.setLoginEnterButton(inventory, this.pendingPasscodes.getOrDefault(uUID, ""), true, bl);
        }
        if ((bukkitTask = this.passcodeOverflowTasks.remove(uUID)) != null) {
            bukkitTask.cancel();
        }
        BukkitTask bukkitTask2 = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            this.passcodeOverflowWarnings.remove(uUID);
            this.passcodeOverflowTasks.remove(uUID);
            if (!player.isOnline() || !this.isAuthLocked(player)) {
                return;
            }
            Inventory openInventory = player.getOpenInventory().getTopInventory();
            if (openInventory.getHolder() instanceof LoginHolder) {
                this.clearPasscodeStatus(uUID);
                LoginHolder loginHolder = (LoginHolder)openInventory.getHolder();
                this.openPasscode(player, loginHolder.input == IdentityInput.CREATE_PASSCODE);
            }
        }, 60L);
        this.passcodeOverflowTasks.put(uUID, bukkitTask2);
    }

    private void showPasscodeStatus(Player player, IdentityInput identityInput, String string, boolean bl) {
        UUID uUID = player.getUniqueId();
        if (bl) {
            this.playHomeSound(player, "failed");
        }
        this.passcodeTitleStatuses.put(uUID, string);
        BukkitTask bukkitTask = this.passcodeStatusTasks.remove(uUID);
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
        if (bl) {
            player.sendMessage((Component)Component.text((String)string, (TextColor)NamedTextColor.DARK_GRAY));
        }
        this.openPasscode(player, identityInput == IdentityInput.CREATE_PASSCODE);
        BukkitTask bukkitTask2 = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            this.passcodeStatusTasks.remove(uUID);
            this.passcodeTitleStatuses.remove(uUID);
            if (!player.isOnline() || !this.isAuthLocked(player)) {
                return;
            }
            Inventory inventory = player.getOpenInventory().getTopInventory();
            InventoryHolder inventoryHolder = inventory.getHolder();
            if (inventoryHolder instanceof LoginHolder) {
                LoginHolder loginHolder = (LoginHolder)inventoryHolder;
                if (loginHolder.input == identityInput) {
                    this.openPasscode(player, identityInput == IdentityInput.CREATE_PASSCODE);
                }
            }
        }, 60L);
        this.passcodeStatusTasks.put(uUID, bukkitTask2);
    }

    private void clearPasscodeStatus(UUID uUID) {
        this.passcodeLifecycleService.clearStatus(uUID, this.passcodeTitleStatuses, this.passcodeStatusTasks);
    }

    private void scheduleLoginSuccess(Player player, String string) {
        UUID uUID = player.getUniqueId();
        if (this.passcodeSuccessTasks.containsKey(uUID)) {
            return;
        }
        this.passcodeTitleStatuses.put(uUID, "verifying.");
        this.openPasscode(player, false);
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory.getHolder() instanceof LoginHolder) {
            inventory.setItem(Ui.Login.ENTER.slot(), this.menuItem(Material.GRAY_STAINED_GLASS_PANE, "Sign in", "verifying."));
        }
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            this.passcodeSuccessTasks.remove(uUID);
            if (!player.isOnline() || !this.isAuthLocked(player)) {
                return;
            }
            if (!string.equals(this.pendingPasscodes.getOrDefault(uUID, ""))) {
                return;
            }
            this.reloadIdentities();
            if (!this.verifyIdentityPasscode(this.identityKey(player), string)) {
                this.showPasscodeStatus(player, IdentityInput.LOGIN, "try again.", true);
                return;
            }
            this.pendingPasscodes.remove(uUID);
            this.finishLoginIdentity(player, true);
        }, 20L);
        this.passcodeSuccessTasks.put(uUID, bukkitTask);
    }

    private void clearPasscodeFeedback(UUID uUID) {
        this.passcodeLifecycleService.clearAll(
                uUID,
                this.passcodeTitleStatuses,
                this.passcodeStatusTasks,
                this.passcodeSuccessTasks,
                this.passcodeOverflowWarnings,
                this.passcodeOverflowTasks);
    }

    private void submitPasscode(Player player, IdentityInput identityInput) {
        String string = this.pendingPasscodes.getOrDefault(player.getUniqueId(), "");
        String string2 = this.invalidIdentitySecretMessage(string);
        if (string2 != null) {
            this.showPasscodeStatus(player, identityInput, string2, true);
            return;
        }
        String string3 = this.identityKey(player);
        this.reloadIdentities();
        if (identityInput == IdentityInput.CREATE_PASSCODE) {
            if (!this.saveIdentityPasscode(string3, player.getName(), string)) {
                this.showPasscodeStatus(player, identityInput, "try again.", true);
                return;
            }
            this.pendingPasscodes.remove(player.getUniqueId());
            this.finishLoginIdentity(player, true);
            return;
        }
        if (identityInput == IdentityInput.LOGIN && this.verifyIdentityPasscode(string3, string)) {
            this.pendingPasscodes.remove(player.getUniqueId());
            this.finishLoginIdentity(player, true);
            return;
        }
        this.showPasscodeStatus(player, identityInput, "try again.", true);
    }

    private void startPasscodeReset(Player player) {
        if (!player.isOnline() || !this.isAuthLocked(player)) {
            return;
        }
        this.pendingPasscodes.remove(player.getUniqueId());
        this.clearPasscodeFeedback(player.getUniqueId());
        this.pendingIdentityInputs.put(player.getUniqueId(), IdentityInput.RESET_WAITING);
        this.handleResetRequestInput(player);
        this.openResetWaiting(player);
    }

    private void finishLoginIdentity(Player player, boolean bl) {
        this.applyIdentityCompletionCleanup(player, this.identityCompletionService.loginCompletion(bl));
        player.closeInventory();
        this.purgeLoginItems(player);
        if (bl) {
            this.playHomeSound(player, "success");
            player.sendMessage((Component)Component.text((String)this.resultText("youre-in", "you're in."), (TextColor)NamedTextColor.GRAY));
        }
        this.restoreAuthInventory(player);
        this.purgeLoginItems(player);
        this.ensureCubee(player, this.currentServer == ServerId.LOBBY);
        this.unlockSurvivalRecipes(player);
        this.applySavedSkin(player, false);
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.applySavedSkin(player, false), 40L);
    }

    private void finishTrustedIdentity(Player player, boolean bl) {
        BackendIdentityCompletionService.CompletionPlan completionPlan = this.identityCompletionService.trustedCompletion(bl);
        this.applyIdentityCompletionCleanup(player, completionPlan);
        if (completionPlan.surfaceAction() == BackendIdentityCompletionService.SurfaceAction.TRUSTED_SURFACE_DELAYED) {
            Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.finishTrustedSurface(player), 2L);
        } else {
            this.finishTrustedSurface(player);
        }
    }

    private void applyIdentityCompletionCleanup(Player player, BackendIdentityCompletionService.CompletionPlan completionPlan) {
        for (BackendIdentityCompletionService.CleanupAction cleanupAction : completionPlan.cleanupActions()) {
            switch (cleanupAction) {
                case CLEAR_RESET_REQUEST -> this.clearResetRequest(this.identityKey(player));
                case CLEAR_PENDING_INPUT -> this.pendingIdentityInputs.remove(player.getUniqueId());
                case CLEAR_PENDING_PASSCODE -> this.pendingPasscodes.remove(player.getUniqueId());
                case CLEAR_GUIDANCE -> this.passcodeGuidanceShown.remove(player.getUniqueId());
                case CLEAR_PASSCODE_FEEDBACK -> this.clearPasscodeFeedback(player.getUniqueId());
                case UNLOCK_AUTH -> this.authLocked.remove(player.getUniqueId());
                case MARK_AUTHENTICATED -> this.authenticatedIdentities.add(player.getUniqueId());
                case SYNC_ACCESS -> this.syncAccessState(player);
                case CLEAR_RESET_INPUT_COUNT -> this.resetInputCounts.remove(player.getUniqueId());
                case SAVE_SESSION -> this.saveIdentitySession(player);
            }
        }
    }

    private void finishTrustedSurface(Player player) {
        if (!player.isOnline() || this.isAuthLocked(player)) {
            return;
        }
        this.ensureCubee(player, false);
        this.unlockSurvivalRecipes(player);
        if (this.trustedSkinSynced.add(player.getUniqueId())) {
            this.applySavedSkin(player, false);
        }
    }

    private void handleResetRequestInput(Player player) {
        int n = this.resetInputCounts.merge(player.getUniqueId(), 1, Integer::sum);
        this.createResetRequest(player);
        this.sendWaitingStatus(player);
        if (n > 3) {
            player.kick((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
        }
    }

    private void createResetRequest(Player player) {
        this.reloadIdentities();
        String string = this.identityKey(player);
        UUID sessionId = this.resetSessionIds.computeIfAbsent(player.getUniqueId(), ignored -> UUID.randomUUID());
        BackendIdentityResetService.CreateResult result = this.identityResetService.createRequest(
                this.identities, player.getName(), string, this.isBedrockPlayer(player), sessionId,
                this.currentServer.proxyName, this.resetOwnerInstance, this::saveIdentities);
        if (result.created() && this.knownResetRequests.add(result.reference())) {
            this.notifyCareRequest(player.getUniqueId());
        }
    }

    private void scheduleCareRequestWatcher() {
        this.careRequestTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, this::checkCareRequests, 40L, 40L);
    }

    private void checkCareRequests() {
        HashSet<String> hashSet = new HashSet<String>(this.resetRequestTokens());
        for (String string : hashSet) {
            if (!this.knownResetRequests.add(string)) continue;
            this.notifyCareRequest(null);
        }
        this.knownResetRequests.retainAll(hashSet);
        this.reconcileResetWaitingPlayers();
        this.checkIdentityResetGrants();
    }

    private void reconcileResetWaitingPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (this.pendingIdentityInputs.get(uuid) != IdentityInput.RESET_WAITING) continue;
            String identity = this.identityKey(player);
            if (this.identityResetGrantExists(identity)
                    || this.identityResetService.requestExistsForIdentity(this.identities, identity)) continue;
            this.pendingIdentityInputs.put(uuid, IdentityInput.LOGIN);
            this.pendingPasscodes.remove(uuid);
            this.clearPasscodeFeedback(uuid);
            this.openPasscode(player, false);
        }
    }

    private void notifyCareRequest(UUID uUID) {
        TextComponent textComponent = Component.text((String)"The house needs care.", (TextColor)HoneyPalette.DEFAULT_WHITE);
        TextComponent textComponent2 = Component.text((String)"check requests.", (TextColor)NamedTextColor.GRAY);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.isAdmin(player) || this.isAuthLocked(player) || player.getUniqueId().equals(uUID)) continue;
            this.playHomeSound(player, "notification");
            player.sendMessage((Component)textComponent);
            player.sendMessage((Component)textComponent2);
        }
    }

    private void tellIdentityPrompt(Player player) {
        IdentityInput identityInput = this.pendingIdentityInputs.get(player.getUniqueId());
        if (identityInput == IdentityInput.CREATE_PASSCODE) {
            if (this.passcodeGuidanceShown.add(player.getUniqueId())) {
                player.sendMessage((Component)Component.text((String)this.promptText("create-passcode", "Create Passcode"), (TextColor)HoneyPalette.DEFAULT_WHITE));
                player.sendMessage((Component)Component.text((String)this.promptText("passcode-length", "4-8 numbers"), (TextColor)NamedTextColor.GRAY));
            }
        } else if (identityInput == IdentityInput.LOGIN && this.passcodeGuidanceShown.add(player.getUniqueId())) {
            player.sendMessage((Component)Component.text((String)this.promptText("enter-passcode", "Enter Passcode"), (TextColor)HoneyPalette.DEFAULT_WHITE));
        }
    }

    private String invalidIdentitySecretMessage(String string) {
        if (string == null) {
            return this.resultText("try-again", "try again.");
        }
        if (string.length() < 4) {
            return this.resultText("too-short", "too short.");
        }
        if (string.length() > 8) {
            return this.resultText("too-long", "too long.");
        }
        if (!string.matches("\\d+")) {
            return this.resultText("try-again", "try again.");
        }
        return null;
    }

    private boolean validSkinName(String string) {
        return string != null && string.length() >= 3 && string.length() <= 16 && string.matches("[A-Za-z0-9_]+");
    }

    private void startLookInput(Player player) {
        if (this.routeBusyCubeeState(player)) {
            return;
        }
        if (this.isBedrockPlayer(player)) {
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.pendingSkinInputs.add(player.getUniqueId());
        this.beginSkinInputTimeout(player);
        player.closeInventory();
        this.sendGuidance(player, "Name.", "Choose a look.");
    }

    private void handleSkinInput(Player player, String string) {
        if (!player.isOnline() || !this.pendingSkinInputs.remove(player.getUniqueId())) {
            return;
        }
        this.cancelSkinInputTimeout(player.getUniqueId());
        if (this.isBedrockPlayer(player)) {
            player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        if (!this.validSkinName(string)) {
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.activeSkinChanges.add(player.getUniqueId());
        this.beginSkinChangeTimeout(player);
        this.sendWaitingStatus(player);
        this.applySkin(player, string, true);
    }

    private void startPrivateNote(Player player, Player player2) {
        if (!this.peopleActionService.canStartPrivateNote(player, player2)) {
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.pendingPrivateNotes.put(player.getUniqueId(), player2.getUniqueId());
        player.closeInventory();
        this.sendGuidance(player, player2.getName(), "Write a note.");
    }

    private void handlePrivateNoteInput(Player player, String string) {
        UUID uUID = this.pendingPrivateNotes.remove(player.getUniqueId());
        if (uUID == null || !player.isOnline()) {
            return;
        }
        Player player2 = Bukkit.getPlayer((UUID)uUID);
        if (string.isBlank()) {
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        if (!this.peopleActionService.canDeliverPrivateNote(player2)) {
            player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        player2.sendMessage((Component)Component.text((String)player.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE));
        player2.sendMessage((Component)Component.text((String)string, (TextColor)NamedTextColor.GRAY));
        player.sendMessage((Component)Component.text((String)"sent.", (TextColor)NamedTextColor.GRAY));
    }

    private void saveSkinChoice(Player player, String string) {
        this.reloadSkins();
        this.identitySkinService.saveChoice(this.skins, player, this.identityKey(player), string);
        this.saveSkins();
    }

    private void applySavedSkin(Player player, boolean bl) {
        if (!player.isOnline() || this.isBedrockPlayer(player)) {
            return;
        }
        this.reloadSkins();
        if (this.skins == null) {
            return;
        }
        String string4 = this.identitySkinService.savedSkin(this.skins, this.identityKey(player));
        if (!string4.isBlank() && this.validSkinName(string4)) {
            this.applySkin(player, string4, bl);
        }
    }

    private void applySkin(Player player, String string, boolean bl) {
        this.pendingSkinApplies.put(player.getUniqueId(), new PendingSkinApply(string, bl));
        this.skinProtocolService.sendSkinApplyRequest(player, string);
    }

    private void beginSkinChangeTimeout(Player player) {
        UUID uUID = player.getUniqueId();
        this.cancelSkinChangeTimeout(uUID);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            if (this.activeSkinChanges.remove(uUID) && player.isOnline()) {
                this.pendingSkinApplies.remove(uUID);
                player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            this.skinChangeTimeouts.remove(uUID);
        }, 200L);
        this.skinChangeTimeouts.put(uUID, bukkitTask);
    }

    private void beginSkinInputTimeout(Player player) {
        UUID uUID = player.getUniqueId();
        this.cancelSkinInputTimeout(uUID);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            if (this.pendingSkinInputs.remove(uUID) && player.isOnline()) {
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            this.skinInputTimeouts.remove(uUID);
        }, 900L);
        this.skinInputTimeouts.put(uUID, bukkitTask);
    }

    private void cancelSkinInputTimeout(UUID uUID) {
        BukkitTask bukkitTask = this.skinInputTimeouts.remove(uUID);
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    private void cancelSkinChangeTimeout(UUID uUID) {
        BukkitTask bukkitTask = this.skinChangeTimeouts.remove(uUID);
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    private boolean identityRegistered(String string) {
        return this.identityAccountService.registered(this.identities, string);
    }

    private String identityKey(Player player) {
        return (this.isBedrockPlayer(player) ? "bedrock:" : "java:") + player.getName().toLowerCase(Locale.ROOT);
    }

    private boolean peopleShortcutPublic(Player player) {
        return true;
    }

    private boolean shouldKeepCubeeItem(Player player) {
        if (this.currentServer == ServerId.LOBBY) {
            return true;
        }
        return this.cubeeVisible(player);
    }

    private boolean cubeeVisible(Player player) {
        return this.identityPreferenceService.cubeeVisible(this.identities, this.identityKey(player), this.currentServer.proxyName, this.currentServer == ServerId.LOBBY);
    }

    private void setCubeeVisible(Player player, boolean bl) {
        if (this.currentServer == ServerId.LOBBY) {
            return;
        }
        this.reloadIdentities();
        if (this.identities == null) {
            return;
        }
        this.identityPreferenceService.setCubeeVisible(this.identities, this.identityKey(player), this.currentServer.proxyName, bl);
        this.saveIdentities();
    }

    private boolean isSandboxServer() {
        return this.currentServer == ServerId.LOBBY || this.currentServer == ServerId.CREATIVE;
    }

    private boolean sandboxAvailable(Player player) {
        return this.sandboxEnabled() && this.isSandboxServer() && this.sandboxShortcutVisible(player);
    }

    private boolean sandboxShortcutVisible(Player player) {
        return true;
    }

    private boolean isSociallyBusy(Player player) {
        return this.isBusy(player.getUniqueId()) || !this.peopleShortcutPublic(player);
    }

    private boolean saveIdentityPasscode(String string, String string2, String string3) {
        if (this.identities == null || string == null || string.isBlank()) {
            return false;
        }
        this.identityAccountService.savePasscode(this.identities, string, string2, string3);
        this.identityResetService.clearGrant(this.identities, string);
        if (!this.saveIdentities()) {
            return false;
        }
        this.reloadIdentities();
        return this.verifyIdentityPasscode(string, string3) && !this.identityResetGrantExists(string);
    }

    private boolean verifyIdentityPasscode(String string, String string2) {
        return this.identityAccountService.verifyPasscode(this.identities, string, string2);
    }

    private void saveIdentityTransfer(Player player, ServerId serverId) {
        if (this.identities == null || !this.authenticatedIdentities.contains(player.getUniqueId())) {
            return;
        }
        this.reloadIdentities();
        String string = this.identityKey(player);
        if (!this.isBedrockPlayer(player) && !this.identityRegistered(string) || this.identityResetGrantExists(string)) {
            return;
        }
        this.identityTransferService.saveTransfer(this.identities, player, string, serverId.proxyName, this::saveIdentities);
    }

    private boolean acceptIdentityTransfer(Player player) {
        this.reloadIdentities();
        String string = this.identityKey(player);
        if (!this.isBedrockPlayer(player) && !this.identityRegistered(string)) {
            return false;
        }
        return this.identityTransferService.acceptTransfer(this.identities, player, string, this.currentServer.proxyName, this::saveIdentities);
    }

    private void saveIdentitySession(Player player) {
        if (this.identities == null) {
            return;
        }
        this.reloadIdentities();
        String string = this.identityKey(player);
        if (!this.identityRegistered(string) || this.identityResetGrantExists(string)) {
            return;
        }
        this.identitySessionService.saveSession(this.identities, player, string, this.identitySessionMillis(), this::saveIdentities);
    }

    private boolean acceptIdentitySession(Player player) {
        this.reloadIdentities();
        String string = this.identityKey(player);
        if (!this.identityRegistered(string)) {
            return false;
        }
        return this.identitySessionService.acceptSession(this.identities, player, string);
    }

    private void clearResetRequest(String string) {
        if (string == null || string.isBlank()) {
            return;
        }
        this.reloadIdentities();
        if (this.identities == null) {
            return;
        }
        String reference = this.identityResetService.currentReference(this.identities, string);
        if (this.identityResetService.clearRequestForIdentity(this.identities, string, this::saveIdentities)) {
            this.knownResetRequests.remove(reference);
        }
    }

    private void clearResolvedResetRequest(Player player) {
        if (player == null) return;
        String string = this.identityKey(player);
        this.reloadIdentities();
        if (this.identityResetGrantExists(string)) {
            return;
        }
        String reference = this.identityResetService.currentReference(this.identities, string);
        UUID sessionId = this.resetSessionIds.get(player.getUniqueId());
        if (sessionId != null && this.identityResetService.clearRequestForSession(
                this.identities, string, sessionId, this.currentServer.proxyName, this.resetOwnerInstance, this::saveIdentities)) {
            this.knownResetRequests.remove(reference);
        }
    }

    private List<String> resetRequestTokens() {
        this.reloadIdentities();
        return this.identityResetService.requestReferences(this.identities);
    }

    private void recoverStaleResetRequests() {
        if (this.identities == null || this.currentServer == null) return;
        int cleared = this.identityResetService.clearStaleRequests(
                this.identities, this.currentServer.proxyName, this.resetOwnerInstance, this::saveIdentities);
        if (cleared > 0) {
            this.reloadIdentities();
            this.getLogger().info("LemonOS cleared " + cleared + " stale session-bound Reset request(s).");
        }
    }

    private void clearOwnedResetRequests() {
        if (this.identities == null || this.currentServer == null) return;
        this.reloadIdentities();
        int cleared = this.identityResetService.clearOwnedRequests(
                this.identities, this.currentServer.proxyName, this.resetOwnerInstance, this::saveIdentities);
        if (cleared > 0) {
            this.knownResetRequests.removeIf(reference -> !this.identityResetService.requestExistsByReference(this.identities, reference));
        }
    }

    private boolean resetRequestExistsForIdentity(String string) {
        this.reloadIdentities();
        return this.identityResetService.requestExistsForIdentity(this.identities, string);
    }

    private boolean resetRequestExistsByToken(String string) {
        this.reloadIdentities();
        return this.identityResetService.requestExistsByReference(this.identities, string);
    }

    private String resetRequestName(String string) {
        return this.identityResetService.requestName(this.identities, string);
    }

    private String resetRequestKey(String string) {
        return this.identityResetService.requestKey(this.identities, string);
    }

    private void allowResetRequest(Player player, String string) {
        if (!this.requireAdmin(player)) {
            return;
        }
        this.reloadIdentities();
        if (!this.adminResetActionService.canResolve(string)) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            this.openAdminRequests(player, 0);
            return;
        }
        String string2 = this.resetRequestKey(string);
        String string3 = this.resetRequestName(string);
        this.removeIdentityPasscode(string2);
        this.clearIdentityPasses(string2);
        this.setIdentityResetGrant(string2, string3);
        if (!this.identityResetService.removeRequestReference(this.identities, string)) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            this.openAdminRequests(player, 0);
            return;
        }
        if (!this.saveIdentities()) {
            this.reloadIdentities();
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.reloadIdentities();
        if (!this.identityResetGrantExists(string2) || this.resetRequestExistsByToken(string)) {
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.knownResetRequests.remove(string);
        Player player2 = Bukkit.getPlayerExact((String)string3);
        if (player2 != null) {
            this.forceIdentityPasscodeReset(player2);
        }
        player.closeInventory();
        player.sendMessage((Component)Component.text((String)"done.", (TextColor)NamedTextColor.GRAY));
    }

    private void denyResetRequest(Player player, String string) {
        if (!this.requireAdmin(player)) {
            return;
        }
        this.reloadIdentities();
        if (!this.identityResetService.clearRequestReference(this.identities, string, this::saveIdentities)) {
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.knownResetRequests.remove(string);
        player.closeInventory();
        player.sendMessage((Component)Component.text((String)"done.", (TextColor)NamedTextColor.GRAY));
    }

    private void removeIdentityPasscode(String string) {
        this.identityAccountService.removePasscode(this.identities, string);
    }

    private void clearIdentityPasses(String string) {
        if (string == null || string.isBlank() || this.identities == null) {
            return;
        }
        this.identitySessionService.clearSession(this.identities, string);
        this.identityTransferService.clearTransfer(this.identities, string);
    }

    private boolean identityResetGrantExists(String string) {
        return this.identityResetService.grantExists(this.identities, string);
    }

    private void setIdentityResetGrant(String string, String string2) {
        if (string == null || string.isBlank() || this.identities == null) {
            return;
        }
        this.identityResetService.setGrant(this.identities, string, string2);
    }

    private void checkIdentityResetGrants() {
        this.reloadIdentities();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline() || this.isBedrockPlayer(player) || !this.identityResetGrantExists(this.identityKey(player))) continue;
            IdentityInput identityInput = this.pendingIdentityInputs.get(player.getUniqueId());
            if (this.isAuthLocked(player) && identityInput == IdentityInput.CREATE_PASSCODE) continue;
            this.forceIdentityPasscodeReset(player);
        }
    }

    private void forceIdentityPasscodeReset(Player player) {
        if (!player.isOnline()) {
            return;
        }
        this.authenticatedIdentities.remove(player.getUniqueId());
        this.authLocked.add(player.getUniqueId());
        this.hideAuthItems(player);
        this.pendingIdentityInputs.put(player.getUniqueId(), IdentityInput.CREATE_PASSCODE);
        this.pendingPasscodes.remove(player.getUniqueId());
        this.passcodeGuidanceShown.remove(player.getUniqueId());
        this.clearPasscodeFeedback(player.getUniqueId());
        this.resetInputCounts.remove(player.getUniqueId());
        this.tellIdentityPrompt(player);
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.openLogin(player), 40L);
    }

    private void loadIdentities() {
        this.identityFile = this.runtimeLayout.dataFile("identity.yml");
        this.identityLockFile = this.runtimeLayout.dataFile("identity.yml.lock");
        File file = this.identityFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        this.identities = this.yamlStore.load(this.identityFile);
        this.saveIdentities();
    }

    private void loadPlaces() {
        this.placesFile = this.runtimeLayout.dataFile("places.yml");
        File file = this.placesFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        this.places = this.yamlStore.load(this.placesFile);
        boolean bl = false;
        for (ServerId serverId : ServerId.values()) {
            bl |= this.placeStatusService.ensureStatus(this.places, serverId.proxyName, this.placeConfig(serverId, "status", "ready"));
        }
        if (bl) {
            this.savePlaces();
        }
    }

    private void loadSkins() {
        this.skinsFile = this.runtimeLayout.dataFile("skins.yml");
        File file = this.skinsFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        this.skins = this.yamlStore.load(this.skinsFile);
        this.saveSkins();
    }

    private void loadBackups() {
        this.backupsFile = this.runtimeLayout.dataFile("backup.yml");
        File file = this.backupsFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        this.backups = this.yamlStore.load(this.backupsFile);
        this.saveBackups();
    }

    private void loadChunks() {
        this.chunksFile = this.runtimeLayout.dataFile("chunks.yml");
        File file = this.chunksFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        this.chunks = this.yamlStore.load(this.chunksFile);
        boolean bl = false;
        for (ServerId serverId : List.of(ServerId.SURVIVAL, ServerId.CREATIVE)) {
            bl |= this.chunkSettingsService.ensureDefaults(this.chunks, serverId.proxyName, ChunkDimension.WORLD.key, 3000, "idle.");
        }
        if (bl) {
            this.saveChunks();
        }
    }

    private void loadHudData() {
        this.hudDataFile = this.runtimeLayout.dataFile("hud.yml");
        File file = this.hudDataFile.getParentFile();
        if (file != null) {
            file.mkdirs();
        }
        this.hudData = this.yamlStore.load(this.hudDataFile);
        boolean bl = this.ensureHudDataDefaults();
        if (bl) {
            this.saveHudData();
        }
    }

    private boolean ensureHudDataDefaults() {
        return this.hudDataService.ensureDefaults(this.hudData, this.boardDataKeys());
    }

    private List<String> boardDataKeys() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (BackendBoardDefinition boardDefinition : BOARD_DEFINITIONS) {
            arrayList.add(boardDefinition.dataKey());
        }
        return arrayList;
    }

    private void saveHudData() {
        if (this.hudData == null || this.hudDataFile == null) {
            return;
        }
        synchronized (this.hudIoLock) {
            try {
                this.yamlStore.saveAtomic(this.hudData, this.hudDataFile);
            }
            catch (IOException iOException) {
                this.getLogger().warning("Unable to save LemonOS HUD data: " + iOException.getMessage());
            }
        }
    }

    private void recoverRuntimeState() {
        this.cleanupAdminCommandAcks();
        this.cleanupBackupTemps();
        this.recoverChunkStatus();
        this.recoverRestStatus();
    }

    private void recoverRestStatus() {
        if (this.currentServer != ServerId.SURVIVAL && this.currentServer != ServerId.CREATIVE) {
            return;
        }
        this.reloadPlaces();
        if (this.places == null || !this.placeRuntimeStatusService.resting(this.places, this.currentServer.proxyName)) {
            return;
        }
        this.restStateService.reset();
        this.setCurrentPlaceRuntimeStatus("ready");
        this.clearRestingSince();
    }

    private void cleanupAdminCommandAcks() {
        File file2 = this.runtimeLayout.adminCommandDirectory();
        if (!file2.isDirectory()) {
            return;
        }
        File[] fileArray = file2.listFiles((file, string) -> string.endsWith(".ack"));
        if (fileArray == null) {
            return;
        }
        for (File file3 : fileArray) {
            try {
                Files.deleteIfExists(file3.toPath());
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private void cleanupBackupTemps() {
        Path path2 = this.honeyRoot().toPath().resolve("backups");
        if (!Files.isDirectory(path2, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> stream = Files.walk(path2, new FileVisitOption[0]);){
            stream.filter(path -> Files.isRegularFile(path, new LinkOption[0])).filter(path -> path.getFileName().toString().endsWith(".tmp")).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            });
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private void recoverChunkStatus() {
        if (!this.chunksAvailableHere() || this.chunks == null) {
            return;
        }
        String string = this.chunkStatus();
        boolean bl = this.adminChunkActionService.isActiveStatus(string);
        boolean bl2 = bl;
        if (!bl) {
            return;
        }
        Object object = this.chunkyApi();
        if (object == null) {
            this.setChunkStatus("idle.");
            return;
        }
        this.reconcileChunkStatus();
    }

    private void reloadPlaces() {
        if (this.placesFile != null) {
            this.places = this.yamlStore.load(this.placesFile);
        }
    }

    private void reloadSkins() {
        if (this.skinsFile != null) {
            this.skins = this.yamlStore.load(this.skinsFile);
        }
    }

    private void reloadBackups() {
        if (this.backupsFile != null) {
            this.backups = this.yamlStore.load(this.backupsFile);
        }
    }

    private void reloadChunks() {
        if (this.chunksFile != null) {
            this.chunks = this.yamlStore.load(this.chunksFile);
        }
    }

    private void savePlaces() {
        if (this.places == null || this.placesFile == null) {
            return;
        }
        try {
            this.yamlStore.saveAtomic(this.places, this.placesFile);
        }
        catch (IOException iOException) {
            this.getLogger().warning("Unable to save LemonOS place state: " + iOException.getMessage());
        }
    }

    private void saveSkins() {
        if (this.skins == null || this.skinsFile == null) {
            return;
        }
        try {
            this.yamlStore.saveAtomic(this.skins, this.skinsFile);
        }
        catch (IOException iOException) {
            this.getLogger().warning("Unable to save LemonOS skin state: " + iOException.getMessage());
        }
    }

    private void saveBackups() {
        if (this.backups == null || this.backupsFile == null) {
            return;
        }
        try {
            this.yamlStore.saveAtomic(this.backups, this.backupsFile);
        }
        catch (IOException iOException) {
            this.getLogger().warning("Unable to save LemonOS backup state: " + iOException.getMessage());
        }
    }

    private void saveChunks() {
        if (this.chunks == null || this.chunksFile == null) {
            return;
        }
        try {
            this.yamlStore.saveAtomic(this.chunks, this.chunksFile);
        }
        catch (IOException iOException) {
            this.getLogger().warning("Unable to save LemonOS chunk state: " + iOException.getMessage());
        }
    }

    private void reloadIdentities() {
        if (this.identityFile != null) {
            this.identities = this.yamlStore.load(this.identityFile);
        }
    }

    private boolean saveIdentities() {
        if (this.identities == null || this.identityFile == null) {
            return false;
        }
        if (this.identityLockFile == null) {
            this.identityLockFile = new File(this.identityFile.getParentFile(), this.identityFile.getName() + ".lock");
        }
        try {
            File file;
            File file2 = this.identityFile.getParentFile();
            if (file2 != null) {
                file2.mkdirs();
            }
            if ((file = this.identityLockFile.getParentFile()) != null) {
                file.mkdirs();
            }
            try (FileChannel fileChannel = FileChannel.open(this.identityLockFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock fileLock = fileChannel.lock();){
                File file3 = new File(this.identityFile.getParentFile(), this.identityFile.getName() + ".tmp");
                this.identities.save(file3);
                try {
                    Files.move(file3.toPath(), this.identityFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                }
                catch (IOException iOException) {
                    Files.move(file3.toPath(), this.identityFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            return true;
        }
        catch (IOException iOException) {
            this.getLogger().warning("Unable to save LemonOS identity data: " + iOException.getMessage());
            return false;
        }
    }

    public void onPluginMessageReceived(String string, Player player, byte[] byArray) {
        if (!ADMIN_CHANNEL.equals(string)) {
            return;
        }
        this.pluginMessageRouterService.route(player, byArray);
    }

    private void handleProxyOpenCubee(Player player, boolean bl) {
        if (bl) {
            this.handleHelpCommand(player);
            return;
        }
        this.handlePadCommand(player);
    }

    private File honeyRoot() {
        if (this.runtimeLayout == null) {
            this.runtimeLayout = BackendRuntimeLayout.resolve();
        }
        return this.runtimeLayout.runtimeRoot();
    }

    private void loadLemonOSConfig() {
        boolean boardsCreated = !this.runtimeLayout.configFile("boards.yml").isFile();
        boolean atmosphereCreated = !this.runtimeLayout.configFile("atmosphere.yml").isFile();
        BackendConfigBootstrapService.LoadedConfig loaded = this.configBootstrapService.load(this.runtimeLayout);
        this.configFile = loaded.configFile();
        this.messagesFile = loaded.messagesFile();
        this.sharedPlacesConfigFile = loaded.placesFile();
        this.sandboxFile = loaded.sandboxFile();
        this.survivalFile = loaded.survivalFile();
        this.boardsFile = loaded.boardsFile();
        this.atmosphereFile = loaded.atmosphereFile();
        this.boardsFileCreated = boardsCreated;
        this.atmosphereFileCreated = atmosphereCreated;
        this.config = loaded.config();
        this.messages = loaded.messages();
        this.sharedPlacesConfig = loaded.places();
        this.sandbox = loaded.sandbox();
        this.survival = loaded.survival();
        this.boards = loaded.boards();
        this.atmosphere = loaded.atmosphere();
        this.finishLemonOSConfigLoad();
    }

    private void finishLemonOSConfigLoad() {
        this.migrateLemonOSConfig();
        this.boardConfig = new BackendBoardConfig(this.boards);
        this.atmosphereConfig = new BackendAtmosphereConfig(this.atmosphere);
        ArrayList<BackendConfigValidationService.PlacePolicy> placePolicies = new ArrayList<>();
        for (ServerId serverId : ServerId.values()) {
            placePolicies.add(new BackendConfigValidationService.PlacePolicy(serverId.proxyName));
        }
        this.configValidationService.validate(this.config, this.boards, this.atmosphere, this.sandbox, this.survival, this.sharedPlacesConfig, placePolicies);
    }

    private void migrateLemonOSConfig() {
        ArrayList<BackendConfigDefaultGroupService.PlaceDefault> placeDefaults = new ArrayList<>();
        for (ServerId serverId : ServerId.values()) {
            placeDefaults.add(new BackendConfigDefaultGroupService.PlaceDefault(
                    serverId.label, serverId.proxyName, this.defaultPlaceMaterial(serverId).name(), serverId.lore));
        }
        BackendConfigMigrationOrchestrator.Target target = new BackendConfigMigrationOrchestrator.Target(
                this.configFile, this.messagesFile, this.sharedPlacesConfigFile, this.sandboxFile, this.survivalFile,
                this.boardsFile, this.atmosphereFile, this.config, this.messages, this.sharedPlacesConfig,
                this.sandbox, this.survival, this.boards, this.atmosphere,
                this.boardsFileCreated, this.atmosphereFileCreated);
        this.configMigrationOrchestrator.migrate(target, this::applyLegacyConfigDefaults, placeDefaults);
        this.boardsFileCreated = false;
        this.atmosphereFileCreated = false;
    }

    private BackendConfigMigrationOrchestrator.LegacyDefaultResult applyLegacyConfigDefaults() {
        this.boardsConfigDirty = false;
        this.atmosphereConfigDirty = false;
        boolean bl = false;
        bl |= this.mainConfigDefaultService.applyCoreDefaults(this.config);
        bl |= this.mainConfigDefaultService.applyCubeeDefaults(this.config);
        bl |= this.mainConfigDefaultService.applyTabDefaults(this.config);
        bl |= this.setMissing(this.config, "stayed-close.enabled", false);
        bl |= this.setMissing(this.config, "stayed-close.refresh-minutes", 1);
        bl |= this.setMissing(this.config, "stayed-close.top", 5);
        bl |= this.setMissing(this.config, "stayed-close.name-width", 12);
        bl |= this.setMissing(this.config, "stayed-close.title", "Stayclose");
        bl |= this.setMissing(this.config, "stayed-close.subtitle", "where small steps stay.");
        bl |= this.setMissing(this.config, "stayed-close.bottom-line", "time spent here.");
        bl |= this.setMissing(this.config, "stayed-close.display.world", "world");
        bl |= this.setMissing(this.config, "stayed-close.display.x", 5.42);
        bl |= this.setMissing(this.config, "stayed-close.display.y", -60.86);
        bl |= this.setMissing(this.config, "stayed-close.display.z", -8.5);
        bl |= this.setMissing(this.config, "stayed-close.display.yaw", 90.0);
        bl |= this.setMissing(this.config, "stayed-close.display.pitch", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.billboard", "fixed");
        bl |= this.setMissing(this.config, "stayed-close.display.scale", 0.53);
        bl |= this.setMissing(this.config, "stayed-close.display.title-offset-x", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.title-offset-y", 0.15);
        bl |= this.setMissing(this.config, "stayed-close.display.title-offset-z", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.subtitle-offset-y", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.row-start-offset-y", -0.34);
        bl |= this.setMissing(this.config, "stayed-close.display.row-gap", -0.13);
        bl |= this.setMissing(this.config, "stayed-close.display.bottom-offset-y", -1.52);
        bl |= this.setMissing(this.config, "stayed-close.display.bottom-offset-z", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bottom-scale", 0.42);
        bl |= this.setMissing(this.config, "stayed-close.display.bottom-line-width", 260);
        bl |= this.setMissing(this.config, "stayed-close.display.name-offset-z", -0.30);
        bl |= this.setMissing(this.config, "stayed-close.display.value-offset-z", 0.46);
        bl |= this.setMissing(this.config, "stayed-close.display.background-alpha", 0);
        bl |= this.setMissing(this.config, "stayed-close.display.view-range", 32);
        bl |= this.setMissing(this.config, "stayed-close.display.line-width", 220);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.enabled", false);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.scale", 0.53);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.line-width", 260);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.background-alpha", 0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.name-width", 12);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.title-offset-x", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.title-offset-y", 0.27);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.title-offset-z", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.subtitle-offset-x", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.subtitle-offset-y", -0.20);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.subtitle-offset-z", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.row-start-offset-x", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.row-start-offset-y", -0.34);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.row-start-offset-z", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.row-gap", -0.27);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.bottom-offset-x", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.bottom-offset-y", -1.02);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.bottom-offset-z", 0.0);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.bottom-scale", 0.42);
        bl |= this.setMissing(this.config, "stayed-close.display.bedrock.bottom-line-width", 260);
        for (BackendBoardDefinition boardDefinition : BOARD_DEFINITIONS) {
            bl |= this.setBoardDefaults(boardDefinition);
        }
        bl |= this.mainConfigDefaultService.applyRestDefaults(this.config);
        bl |= this.setMissing(this.config, "atmosphere.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.actionbar.duration-seconds", 4);
        bl |= this.setMissing(this.config, "atmosphere.actionbar.repeat-seconds", 1);
        bl |= this.setMissing(this.config, "atmosphere.actionbar.cooldown-seconds", 360);
        bl |= this.setMissing(this.config, "atmosphere.activity.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.activity.global-cooldown-seconds", 90);
        bl |= this.setMissing(this.config, "atmosphere.activity.break-blocks.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.activity.break-blocks.threshold", 500);
        bl |= this.setMissing(this.config, "atmosphere.activity.break-blocks.cooldown-seconds", 240);
        bl |= this.setMissing(this.config, "atmosphere.activity.place-blocks.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.activity.place-blocks.threshold", 350);
        bl |= this.setMissing(this.config, "atmosphere.activity.place-blocks.cooldown-seconds", 240);
        bl |= this.setMissing(this.config, "atmosphere.activity.pickup-items.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.activity.pickup-items.threshold", 240);
        bl |= this.setMissing(this.config, "atmosphere.activity.pickup-items.cooldown-seconds", 240);
        bl |= this.setMissing(this.config, "atmosphere.activity.craft-items.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.activity.craft-items.threshold", 64);
        bl |= this.setMissing(this.config, "atmosphere.activity.craft-items.cooldown-seconds", 240);
        bl |= this.setMissing(this.config, "atmosphere.activity.damage-survived.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.activity.damage-survived.threshold", 7);
        bl |= this.setMissing(this.config, "atmosphere.activity.damage-survived.cooldown-seconds", 240);
        bl |= this.setMissing(this.config, "atmosphere.activity.session-minutes.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.activity.session-minutes.threshold", 30);
        bl |= this.setMissing(this.config, "atmosphere.activity.session-minutes.cooldown-seconds", 240);
        bl |= this.setMissing(this.config, "atmosphere.music.enabled", true);
        bl |= this.setMissing(this.config, "atmosphere.music.delay-seconds", 8);
        bl |= this.setMissing(this.config, "atmosphere.music.gap-seconds", 20);
        bl |= this.setMissing(this.config, "atmosphere.music.volume", 0.16);
        bl |= this.setMissing(this.config, "atmosphere.music.pitch", 1.0);
        bl |= this.setMissing(this.config, "atmosphere.music.actionbar.enabled", false);
        bl |= this.setMissing(this.config, "atmosphere.music.actionbar.format", "playing {music}");
        bl |= this.setMissing(this.config, "atmosphere.music.actionbar.refresh-ticks", 20);
        bl |= this.setMissing(this.config, "atmosphere.music.actionbar.resume-delay-ticks", 80);
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_CAT", "C418 - cat");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_CHIRP", "C418 - chirp");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_FAR", "C418 - far");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_MALL", "C418 - mall");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_STRAD", "C418 - strad");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_WAIT", "C418 - wait");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_CREATOR_MUSIC_BOX", "Lena Raine - Creator");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_LAVA_CHICKEN", "Hyper Potions - Lava Chicken");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_BOUNCE", "fingerspit - Bounce");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_RELIC", "Aaron Cherof - Relic");
        bl |= this.setMissing(this.config, "atmosphere.music.display-names.MUSIC_DISC_OTHERSIDE", "Lena Raine - otherside");
        bl |= this.setMissing(this.config, "atmosphere.music.weight.warm", 80);
        bl |= this.setMissing(this.config, "atmosphere.music.weight.playful", 15);
        bl |= this.setMissing(this.config, "atmosphere.music.weight.special", 5);
        bl |= this.setMissing(this.config, "atmosphere.music.tracks.warm", List.of("MUSIC_DISC_CAT", "MUSIC_DISC_CHIRP", "MUSIC_DISC_FAR", "MUSIC_DISC_MALL", "MUSIC_DISC_STRAD", "MUSIC_DISC_WAIT", "MUSIC_DISC_CREATOR_MUSIC_BOX"));
        bl |= this.setMissing(this.config, "atmosphere.music.tracks.playful", List.of("MUSIC_DISC_LAVA_CHICKEN", "MUSIC_DISC_BOUNCE"));
        bl |= this.setMissing(this.config, "atmosphere.music.tracks.special", List.of("MUSIC_DISC_RELIC", "MUSIC_DISC_OTHERSIDE"));
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_CAT", 185);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_CHIRP", 185);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_FAR", 174);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_MALL", 197);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_STRAD", 188);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_WAIT", 238);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_CREATOR_MUSIC_BOX", 73);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_LAVA_CHICKEN", 135);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_BOUNCE", 155);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_RELIC", 218);
        bl |= this.setMissing(this.config, "atmosphere.music.track-seconds.MUSIC_DISC_OTHERSIDE", 195);
        bl |= this.setMissing(this.config, "sounds.enabled", true);
        bl |= this.setMissing(this.config, "sounds.ui-click.sound", "block.note_block.hat");
        bl |= this.setMissing(this.config, "sounds.ui-click.volume", 0.20);
        bl |= this.setMissing(this.config, "sounds.ui-click.pitch", 1.2);
        bl |= this.setMissing(this.config, "sounds.numpad-press.sound", "block.copper_bulb.turn_on");
        bl |= this.setMissing(this.config, "sounds.numpad-press.volume", 0.30);
        bl |= this.setMissing(this.config, "sounds.numpad-press.pitch", 1.25);
        bl |= this.setMissing(this.config, "sounds.success.sound", "entity.experience_orb.pickup");
        bl |= this.setMissing(this.config, "sounds.success.volume", 0.24);
        bl |= this.setMissing(this.config, "sounds.success.pitch", 1.1);
        bl |= this.setMissing(this.config, "sounds.failed.sound", "block.note_block.bass");
        bl |= this.setMissing(this.config, "sounds.failed.volume", 0.35);
        bl |= this.setMissing(this.config, "sounds.failed.pitch", 0.7);
        bl |= this.setMissing(this.config, "sounds.notification.sound", "block.note_block.bell");
        bl |= this.setMissing(this.config, "sounds.notification.volume", 0.22);
        bl |= this.setMissing(this.config, "sounds.notification.pitch", 1.0);
        bl |= this.setMissing(this.config, "sounds.sandbox-first.sound", "block.wooden_button.click_on");
        bl |= this.setMissing(this.config, "sounds.sandbox-first.volume", 0.60);
        bl |= this.setMissing(this.config, "sounds.sandbox-first.pitch", 1.1);
        bl |= this.setMissing(this.config, "sounds.sandbox-second.sound", "block.wooden_button.click_off");
        bl |= this.setMissing(this.config, "sounds.sandbox-second.volume", 0.60);
        bl |= this.setMissing(this.config, "sounds.sandbox-second.pitch", 1.15);
        bl |= this.setMissing(this.config, "sounds.sandbox-done.sound", "entity.experience_orb.pickup");
        bl |= this.setMissing(this.config, "sounds.sandbox-done.volume", 0.22);
        bl |= this.setMissing(this.config, "sounds.sandbox-done.pitch", 1.1);
        return new BackendConfigMigrationOrchestrator.LegacyDefaultResult(
                bl, this.boardsConfigDirty, this.atmosphereConfigDirty);
    }

    private boolean setMissing(FileConfiguration fileConfiguration, String string, Object object) {
        if (fileConfiguration == this.config && string != null && string.startsWith("atmosphere.")) {
            boolean changed = this.configMigrationService.setMissing(this.atmosphere, string, object);
            this.atmosphereConfigDirty |= changed;
            return false;
        }
        if (fileConfiguration == this.config && string != null && string.startsWith("stayed-close.")) {
            boolean changed = this.configMigrationService.setMissing(this.boards, this.legacyBoardPath(string), object);
            this.boardsConfigDirty |= changed;
            return false;
        }
        return this.configMigrationService.setMissing(fileConfiguration, string, object);
    }

    private boolean setBoardDefaults(BackendBoardDefinition boardDefinition) {
        BackendHudConfigMigrationService.Board board = new BackendHudConfigMigrationService.Board(
                boardDefinition.dataKey(),
                boardDefinition.configPath(),
                boardDefinition.defaultTitle(),
                boardDefinition.defaultSubtitle(),
                boardDefinition.defaultBottomLine(),
                boardDefinition.defaultX(),
                boardDefinition.defaultY(),
                boardDefinition.defaultZ(),
                boardDefinition.trackBlocksChanged());
        boolean changed = this.hudConfigMigrationService.setBoardDefaults(this.boards, board);
        this.boardsConfigDirty |= changed;
        return false;
    }

    private boolean configBoolean(String string, boolean bl) {
        return this.config == null ? bl : this.config.getBoolean(string, bl);
    }

    private String configString(String string, String string2) {
        return this.config == null ? string2 : this.config.getString(string, string2);
    }

    private List<String> configStringList(String string, List<String> list) {
        if (this.config == null || !this.config.isList(string)) {
            return list;
        }
        List<String> list2 = this.config.getStringList(string);
        return list2 == null ? list : list2;
    }

    private int configInt(String string, int n, int n2, int n3) {
        int n4 = this.config == null ? n : this.config.getInt(string, n);
        return Math.max(n2, Math.min(n3, n4));
    }

    private double configDouble(String string, double d, double d2, double d3) {
        double d4 = this.config == null ? d : this.config.getDouble(string, d);
        return Math.max(d2, Math.min(d3, d4));
    }

    private double configDouble(String string, String string2, double d, double d2, double d3) {
        if (this.config == null || this.config.contains(string)) {
            return this.configDouble(string, d, d2, d3);
        }
        return this.configDouble(string2, d, d2, d3);
    }

    private String legacyBoardPath(String path) {
        if (path == null) return "";
        if (path.startsWith("stayed-close.")) return "boards." + path;
        if (path.startsWith("hud.")) return "boards." + path.substring("hud.".length());
        return path;
    }

    private int sandboxInt(String string, int n, int n2, int n3) {
        int n4 = this.sandbox == null ? n : this.sandbox.getInt(string, n);
        return Math.max(n2, Math.min(n3, n4));
    }

    private boolean survivalBoolean(String string, boolean bl) {
        return this.survival == null ? bl : this.survival.getBoolean(string, bl);
    }

    private int survivalInt(String string, int n, int n2, int n3) {
        int n4 = this.survival == null ? n : this.survival.getInt(string, n);
        return Math.max(n2, Math.min(n3, n4));
    }

    private boolean survivalFeatureEnabled(String string) {
        return this.survivalBoolean("survival." + string + ".enabled", false);
    }

    private boolean survivalSneakAllowed(Player player, String string) {
        return !this.survivalBoolean("survival." + string + ".require-sneak", true) || player.isSneaking();
    }

    private String messageText(String string, String string2) {
        return this.messages == null ? string2 : this.messages.getString(string, string2);
    }

    private String promptText(String string, String string2) {
        return this.messageText("prompts." + string, string2);
    }

    private String resultText(String string, String string2) {
        return this.messageText("results." + string, string2);
    }

    private void playHomeSound(Player player, String string) {
        if (player == null || !player.isOnline() || !this.configBoolean("sounds.enabled", true)) {
            return;
        }
        String string2 = "sounds." + string + ".";
        String string3 = this.configString(string2 + "sound", "");
        if (string3 == null || string3.isBlank()) {
            return;
        }
        float f = (float)this.configDouble(string2 + "volume", 0.18, 0.0, 1.0);
        float f2 = (float)this.configDouble(string2 + "pitch", 1.0, 0.5, 2.0);
        if (f <= 0.0f) {
            return;
        }
        try {
            player.playSound((Entity)player, string3, f, f2);
        }
        catch (RuntimeException runtimeException) {
            this.getLogger().warning("LemonOS sound '" + string3 + "' for " + string + " is unavailable.");
        }
    }

    private int cubeeSlot() {
        return this.configInt("cubee.slot", 8, 0, 8);
    }

    private boolean cubeeEnabled() {
        return this.configBoolean("cubee.enabled", true);
    }

    private boolean lookEnabled() {
        return this.configBoolean("look.enabled", false);
    }

    private boolean sandboxEnabled() {
        return this.sandbox != null && this.sandbox.getBoolean("sandbox.enabled", false);
    }

    private boolean javaLoginEnabled() {
        return this.configBoolean("auth.java-login", true);
    }

    private boolean bedrockTrusted() {
        return this.configBoolean("auth.bedrock-trusted", true);
    }

    private long identitySessionMillis() {
        int n = this.configInt("auth.session-minutes", 10, 0, 10080);
        return (long)n * 60000L;
    }

    private boolean hideCommandSuggestions() {
        return this.configBoolean("ui.hidden-command-suggestions", true);
    }

    private int sandboxMaxBlocks() {
        return this.sandboxInt("sandbox.max-blocks", 32768, 1, 32768);
    }

    private int sandboxHistoryLimit() {
        return this.sandboxInt("sandbox.history-limit", 30, 1, 100);
    }

    private Material sandboxMaterial(String string, Material material) {
        if (this.sandbox == null) {
            return material;
        }
        return this.materialValue(this.sandbox.getString("sandbox." + string, material.name()), material);
    }

    private Material basicToolMaterial() {
        return this.sandboxMaterial("basic-tool", Material.WOODEN_AXE);
    }

    private Material moreToolMaterial() {
        return this.sandboxMaterial("more-tool", Material.WOODEN_HOE);
    }

    private Material defaultSandboxMaterial() {
        return this.sandboxMaterial("default-material", Material.STONE);
    }

    private Material replaceSourceMaterial() {
        return this.sandboxMaterial("replace-source-material", Material.STONE);
    }

    private Material replaceTargetMaterial() {
        return this.sandboxMaterial("replace-target-material", Material.OAK_PLANKS);
    }

    private Material placeMaterial(ServerId serverId) {
        Material material = this.defaultPlaceMaterial(serverId);
        return this.materialValue(this.placeConfig(serverId, "item", material.name()), material);
    }

    private String placeName(ServerId serverId) {
        return this.placeConfig(serverId, "name", serverId.label);
    }

    private String placeConfig(ServerId serverId, String string, String string2) {
        return this.sharedPlacesConfig == null ? string2 : this.sharedPlacesConfig.getString("places." + serverId.proxyName + "." + string, string2);
    }

    private String placeSpawnWorld(ServerId serverId) {
        return this.placeConfig(serverId, "spawn-world", "world");
    }

    private Material defaultPlaceMaterial(ServerId serverId) {
        return switch (serverId.ordinal()) {
            default -> Material.OAK_LOG;
            case 1 -> Material.GRASS_BLOCK;
            case 2 -> Material.CRAFTING_TABLE;
        };
    }

    private Material materialValue(String string, Material material) {
        Material material2 = string == null ? null : Material.matchMaterial((String)string.trim().toUpperCase(Locale.ROOT).replace(' ', '_'));
        return material2 == null ? material : material2;
    }

    private void scheduleAdminCommandQueue() {
        this.adminCommandTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, this::drainAdminCommandQueue, 20L, 20L);
    }

    private void drainAdminCommandQueue() {
        List<String> list;
        File file = this.adminCommandQueueFile();
        if (!file.isFile()) {
            return;
        }
        try {
            list = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            Files.writeString(file.toPath(), (CharSequence)"", StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (IOException iOException) {
            return;
        }
        for (String string : list) {
            String string2 = this.queuedCommandId(string);
            String string3 = this.queuedCommand(string);
            if (string3.isBlank() || this.containsCommandChain(string3) || !this.isAllowedQueuedAdminCommand(string3)) {
                this.writeAdminCommandAck(string2, false);
                continue;
            }
            String string4 = string3.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
            boolean bl = Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)this.namespacedAdminCommand(string4, string3));
            this.writeAdminCommandAck(string2, bl);
        }
    }

    private File adminCommandQueueFile() {
        return this.runtimeLayout.adminCommandQueue(this.currentServer.proxyName);
    }

    private File adminCommandAckFile() {
        return this.runtimeLayout.adminCommandAck(this.currentServer.proxyName);
    }

    private String queuedCommandId(String string) {
        if (string == null) {
            return "";
        }
        int n = string.indexOf(9);
        if (n < 0) {
            return "";
        }
        int n2 = string.indexOf(9, n + 1);
        if (n2 < 0) {
            return "";
        }
        return string.substring(n + 1, n2).trim();
    }

    private String queuedCommand(String string) {
        if (string == null) {
            return "";
        }
        int n = string.indexOf(9);
        if (n < 0 || n + 1 >= string.length()) {
            return string.trim();
        }
        int n2 = string.indexOf(9, n + 1);
        if (n2 >= 0 && n2 + 1 < string.length()) {
            return string.substring(n2 + 1).trim();
        }
        return string.substring(n + 1).trim();
    }

    private void writeAdminCommandAck(String string, boolean bl) {
        if (string == null || string.isBlank()) {
            return;
        }
        File file = this.adminCommandAckFile();
        File file2 = file.getParentFile();
        if (file2 != null) {
            file2.mkdirs();
        }
        String string2 = string + "\t" + (bl ? "ok" : "fail") + System.lineSeparator();
        try {
            Files.writeString(file.toPath(), (CharSequence)string2, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private boolean isAllowedQueuedAdminCommand(String string) {
        String[] stringArray = string.trim().split("\\s+");
        if (stringArray.length == 0) {
            return false;
        }
        String string2 = stringArray[0].toLowerCase(Locale.ROOT);
        if (string2.equals("time")) {
            return stringArray.length == 3 && stringArray[1].equalsIgnoreCase("set") && this.isTimeValue(stringArray[2]);
        }
        if (string2.equals("weather")) {
            return stringArray.length >= 2 && stringArray.length <= 3 && Set.of("clear", "rain", "thunder").contains(stringArray[1].toLowerCase(Locale.ROOT)) && (stringArray.length == 2 || stringArray[2].matches("[0-9]{1,7}"));
        }
        if (string2.equals("difficulty")) {
            return stringArray.length == 2 && Set.of("peaceful", "easy", "normal", "hard").contains(stringArray[1].toLowerCase(Locale.ROOT));
        }
        if (string2.equals("gamemode")) {
            return stringArray.length >= 2 && stringArray.length <= 3 && Set.of("survival", "creative", "adventure", "spectator").contains(stringArray[1].toLowerCase(Locale.ROOT)) && (stringArray.length == 2 || this.safeAdminName(stringArray[2]));
        }
        if (string2.equals("tp") || string2.equals("teleport")) {
            return stringArray.length >= 2 && stringArray.length <= 5 && this.safeAdminTokens(stringArray);
        }
        if (string2.equals("give")) {
            return stringArray.length >= 3 && stringArray.length <= 4 && this.safeAdminName(stringArray[1]) && this.safeAdminMaterial(stringArray[2]) && (stringArray.length == 3 || stringArray[3].matches("[0-9]{1,4}"));
        }
        if (string2.equals("effect")) {
            return stringArray.length >= 3 && stringArray.length <= 6 && this.safeAdminTokens(stringArray);
        }
        return string2.equals("chunky") && this.isAllowedQueuedChunky(stringArray);
    }

    private boolean isTimeValue(String string) {
        if (Set.of("day", "noon", "night", "midnight").contains(string.toLowerCase(Locale.ROOT))) {
            return true;
        }
        try {
            int n = Integer.parseInt(string);
            return n >= 0 && n <= 24000;
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    private boolean isAllowedQueuedChunky(String[] stringArray) {
        if (stringArray.length == 2) {
            return Set.of("progress", "start", "cancel", "spawn").contains(stringArray[1].toLowerCase(Locale.ROOT));
        }
        if (stringArray.length == 3 && stringArray[1].equalsIgnoreCase("radius")) {
            try {
                int n = Integer.parseInt(stringArray[2]);
                return n >= 1 && n <= 100000;
            }
            catch (NumberFormatException numberFormatException) {
                return false;
            }
        }
        return stringArray.length == 3 && stringArray[1].equalsIgnoreCase("world") && this.safeAdminMaterial(stringArray[2]);
    }

    private boolean safeAdminTokens(String[] stringArray) {
        for (String string : stringArray) {
            if (string.matches("[A-Za-z0-9_:@.\\-~^=,\\[\\]]{1,64}")) continue;
            return false;
        }
        return true;
    }

    private boolean safeAdminName(String string) {
        return string != null && string.matches("\\.?[A-Za-z0-9_]{1,32}");
    }

    private boolean safeAdminMaterial(String string) {
        return string != null && string.matches("[A-Za-z0-9_:\\-]{1,64}");
    }

    private void openCubee(Player player) {
        this.syncAccessState(player);
        if (this.travelStateService.contains(player.getUniqueId())) {
            this.cancelTravel(player, true);
            return;
        }
        if (this.pendingSkinInputs.remove(player.getUniqueId())) {
            this.cancelSkinInputTimeout(player.getUniqueId());
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        if (this.activeSkinChanges.remove(player.getUniqueId())) {
            this.cancelSkinChangeTimeout(player.getUniqueId());
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        if (this.pendingPrivateNotes.remove(player.getUniqueId()) != null) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        if (this.cancelActiveDrawing(player.getUniqueId())) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        for (BackendSandboxPreviewLifecycleService.PreviewKind previewKind : this.sandboxPreviewLifecycleService.recoveryOrder()) {
            if (this.hasPreview(player.getUniqueId(), previewKind)) {
                this.openRecoveryTick(() -> this.openPreviewConfirm(player, previewKind));
                return;
            }
        }
        if (this.meetRequestService.incoming(player.getUniqueId()) != null) {
            this.openRequests(player);
            return;
        }
        if (this.meetRequestService.consumeLate(player.getUniqueId())) {
            player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        if (this.defaultLaunchPending.remove(player.getUniqueId())) {
            this.openDefaultLaunchPage(player);
            return;
        }
        this.openCubeeLaunchPlan(player, this.cubeeRoutingService.rememberedSurfacePlan(
                this.currentCubeeSurface(player),
                this.currentCubeeRoot(player),
                this.currentServer == ServerId.LOBBY,
                this.peopleShortcutPublic(player),
                this.isAdmin(player),
                this.sandboxAvailable(player),
                this.isBedrockPlayer(player)));
    }

    private void openDefaultLaunchPage(Player player) {
        this.openCubeeLaunchPlan(player, this.cubeeRoutingService.defaultLaunchPlan(
                this.currentServer == ServerId.LOBBY,
                this.currentServer == ServerId.SURVIVAL,
                this.currentServer == ServerId.CREATIVE,
                this.peopleShortcutPublic(player),
                this.sandboxAvailable(player),
                this.isBedrockPlayer(player)));
    }

    private void openCubeeLaunchPlan(Player player, BackendCubeeRoutingService.LaunchPlan launchPlan) {
        if (launchPlan.root() != null) {
            this.switchCubeeRoot(player, launchPlan.root());
        }
        if (launchPlan.surface() != null) {
            this.switchCubeeSurface(player, launchPlan.surface());
        }
        switch (launchPlan.action()) {
            case OPEN_GO -> this.openGo(player);
            case OPEN_LAST_PEOPLE -> this.openLastPeople(player);
            case OPEN_LAST_ADMIN_PEOPLE -> this.openLastAdminPeople(player);
            case OPEN_DRAWING -> this.openDrawing(player);
            case OPEN_ADMIN -> this.openAdmin(player);
            case OPEN_BEDROCK_HOME -> this.openBedrockHome(player);
            case OPEN_JAVA_HOME -> player.openInventory(this.createJavaHomeInventory(player));
        }
    }

    private boolean shouldOpenBedrockPage(Player player) {
        return this.bedrockPageRouteService.route(this.isBedrockPlayer(player)).bedrock();
    }

    private Inventory createJavaHomeInventory(Player player) {
        this.switchCubeeSurface(player, CubeeSurface.HOME);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.HOME), (int)27, this.homeTitleComponent());
        this.setGeneralNav(inventory, false, null);
        if (this.lookEnabled()) {
            this.setButton(inventory, Ui.Home.LOOK);
        }
        if (this.currentServer != ServerId.LOBBY && this.peopleShortcutPublic(player)) {
            this.setButtonLore(inventory, Ui.Home.PEOPLE, this.loreLines("stay close.", this.homePeopleStatus(player)));
        }
        this.setButton(inventory, Ui.Home.PLACES);
        if (this.currentServer == ServerId.SURVIVAL) {
            this.setButtonLore(inventory, Ui.Home.HOME, this.survivalHomeLore(player));
        } else if (this.sandboxAvailable(player)) {
            this.setButton(inventory, Ui.Home.SANDBOX);
        }
        if (this.isAdmin(player)) {
            this.setHomeCareButton(inventory, player);
        }
        return inventory;
    }

    private String homeTitle() {
        return "Home";
    }

    private Component homeTitleComponent() {
        return Component.text((String)"Home", (TextColor)HoneyPalette.DEFAULT_WHITE);
    }

    private String careTitle() {
        return "Care";
    }

    private Component careTitleComponent() {
        return Component.text((String)"Care", (TextColor)HoneyPalette.DEFAULT_WHITE);
    }

    private CubeeRoot currentCubeeRoot(Player player) {
        return this.cubeeNavigationService.root(player.getUniqueId());
    }

    private CubeeSurface currentCubeeSurface(Player player) {
        return this.cubeeNavigationService.surface(player.getUniqueId());
    }

    private void switchCubeeRoot(Player player, CubeeRoot cubeeRoot) {
        this.cubeeNavigationService.switchRoot(player.getUniqueId(), cubeeRoot);
        if (cubeeRoot != CubeeRoot.CARE) {
            this.clearCareWorldStatus(player.getUniqueId());
        }
    }

    private void switchCubeeSurface(Player player, CubeeSurface cubeeSurface) {
        this.cubeeNavigationService.switchSurface(player.getUniqueId(), cubeeSurface);
    }

    private Ui.ButtonSpec homeCareButton(Player player) {
        return Ui.Home.CARE;
    }

    private void setHomeCareButton(Inventory inventory, Player player) {
        this.setButton(inventory, this.homeCareButton(player));
    }

    private int currentPeoplePageIndex(Player player) {
        return this.peopleNavigationService.currentPageIndex(player);
    }

    private void rememberPeoplePageIndex(Player player, int n) {
        this.peopleNavigationService.rememberPageIndex(player, n);
    }

    private void openLastPeople(Player player) {
        this.openPeople(player, this.currentPeoplePageIndex(player));
    }

    private int currentAdminPeoplePageIndex(Player player) {
        return this.adminPeopleNavigationService.currentPageIndex(player);
    }

    private void rememberAdminPeoplePageIndex(Player player, int n) {
        this.adminPeopleNavigationService.rememberPageIndex(player, n);
    }

    private void openLastAdminPeople(Player player) {
        this.openAdminPeople(player, this.currentAdminPeoplePageIndex(player));
    }

    private void openGo(Player player) {
        if (this.routeBusyCubeeState(player)) {
            return;
        }
        this.switchCubeeRoot(player, CubeeRoot.CUBEE);
        this.switchCubeeSurface(player, CubeeSurface.PLACES);
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockGo(player);
            return;
        }
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.GO), (int)27, (Component)Component.text((String)"Places", (TextColor)HoneyPalette.DEFAULT_WHITE));
        List<ServerId> list = this.goTargets();
        for (int i = 0; i < list.size(); ++i) {
            this.setPlaceButton(inventory, 12 + i, list.get(i));
        }
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void handleCubeeClick(Player player, CubeePage cubeePage, int n) {
        BackendCubeePageAccessService.AccessPolicy accessPolicy = this.cubeePageAccessService.policy(cubeePage.name());
        if (accessPolicy.adminRequired() && !this.requireAdmin(player)) {
            return;
        }
        if (accessPolicy.chunkEditRequired() && !this.adminChunkActionService.canEditChunks(this.chunksRunning())) {
            return;
        }
        if (cubeePage == CubeePage.HOME) {
            this.handleHomePageClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.GO) {
            this.handlePlacesClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.PEOPLE) {
            this.handlePeoplePageClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.PLAYER) {
            this.handlePlayerPageClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.REQUESTS) {
            this.handleRequestsClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.DRAWING) {
            this.handleSandboxClick(player, this.sandboxInteractionService.action(n));
            return;
        }
        if (cubeePage == CubeePage.CLONE_CONFIRM) {
            this.handleSandboxConfirmClick(player, n, () -> this.placeClone(player), () -> this.cancelClonePreview(player.getUniqueId()));
            return;
        }
        if (cubeePage == CubeePage.CLEAR_CONFIRM) {
            this.handleSandboxConfirmClick(player, n, () -> this.placeClear(player), () -> this.cancelClearPreview(player.getUniqueId()));
            return;
        }
        if (cubeePage == CubeePage.ROTATE_CONFIRM) {
            this.handleSandboxConfirmClick(player, n, () -> this.placeRotate(player), () -> this.cancelRotatePreview(player.getUniqueId()));
            return;
        }
        if (cubeePage == CubeePage.FLIP_CONFIRM) {
            this.handleSandboxConfirmClick(player, n, () -> this.placeFlip(player), () -> this.cancelFlipPreview(player.getUniqueId()));
            return;
        }
        if (cubeePage == CubeePage.ADMIN) {
            this.handleAdminRootClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_KEYS) {
            this.handleAdminKeysClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_KEY_HOLDERS) {
            this.handleAdminKeyHoldersClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_KEY_HOLDER) {
            this.handleAdminKeyHolderClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_KEY_TAKE_CONFIRM) {
            this.handleAdminKeyTakeConfirmClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_KEY_PEOPLE) {
            this.handleAdminKeyPeopleClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_KEY_GIVE) {
            this.handleAdminKeyGiveClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_SELF) {
            this.handleAdminSelfClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_SELF_CLEAR_CONFIRM) {
            this.handleAdminSelfClearConfirmClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_PEOPLE) {
            this.handleAdminPeopleClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_REQUESTS) {
            this.handleAdminRequestsClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_RESET) {
            this.handleAdminResetClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_ATMOSPHERE) {
            this.handleAdminAtmosphereClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_UPKEEP) {
            this.handleAdminUpkeepClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_CHUNKS) {
            this.handleAdminChunksClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_CHUNKS_DIMENSION) {
            this.handleAdminChunksDimensionClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_CHUNKS_SIZE) {
            this.handleAdminChunksSizeClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_CHUNKS_CONFIRM) {
            this.handleAdminChunksConfirmClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_BACKUP_CONFIRM) {
            this.handleAdminBackupConfirmClick(player, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_GAMEMODE) {
            CubeeHolder cubeeHolder = this.currentHolder(player);
            Player player5 = cubeeHolder == null || cubeeHolder.subject == null ? player : Bukkit.getPlayer((UUID)cubeeHolder.subject);
            int n20 = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
            if (!this.adminPlayerControlService.canOpenGamemode(player5)) {
                this.openNextTick(() -> this.openAdminPeople(player, n20));
                return;
            }
            this.handleAdminGamemodeClick(player, player5, n20, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_PLAYER) {
            CubeeHolder cubeeHolder = this.currentHolder(player);
            Player player6 = cubeeHolder == null ? null : Bukkit.getPlayer((UUID)cubeeHolder.subject);
            int n23 = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
            this.handleAdminPlayerClick(player, player6, n23, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_PLAYER_CONTROL) {
            CubeeHolder cubeeHolder = this.currentHolder(player);
            Player player7 = cubeeHolder == null ? null : Bukkit.getPlayer((UUID)cubeeHolder.subject);
            int n25 = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
            this.handleAdminPlayerControlClick(player, player7, n25, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_PLAYER_SEND_PLACES) {
            CubeeHolder holder = this.currentHolder(player);
            Player target = holder == null || holder.subject == null ? null : Bukkit.getPlayer((UUID)holder.subject);
            int pageIndex = holder == null ? 0 : holder.pageIndex;
            if (!this.adminPeopleActionService.canTarget(player, target)) {
                this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                return;
            }
            this.handleAdminPlayerSendPlacesClick(player, target, pageIndex, n);
            return;
        }
        if (cubeePage == CubeePage.ADMIN_PLAYER_CLEAR_CONFIRM) {
            CubeeHolder cubeeHolder = this.currentHolder(player);
            Player player8 = cubeeHolder == null ? null : Bukkit.getPlayer((UUID)cubeeHolder.subject);
            int n27 = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
            if (!this.adminPeopleActionService.canTarget(player, player8)) {
                this.openNextTick(() -> this.openAdminPeople(player, n27));
                return;
            }
            this.handleAdminPlayerClearConfirmClick(player, player8, n);
        }
    }

    private void handleHomePageClick(Player player, int slot) {
        BackendCubeeRoutingService.HomeAction homeAction = this.cubeeRoutingService.homeAction(
                slot,
                Ui.Home.LOOK.slot(),
                Ui.Home.CARE.slot(),
                this.lookEnabled(),
                this.isAdmin(player),
                this.currentServer == ServerId.LOBBY,
                this.currentServer == ServerId.SURVIVAL,
                this.peopleShortcutPublic(player),
                this.sandboxAvailable(player));
        switch (homeAction) {
            case LOOK -> this.startLookInput(player);
            case CARE -> {
                this.switchCubeeRoot(player, CubeeRoot.CARE);
                this.openAdmin(player);
            }
            case PEOPLE -> this.openNextTick(() -> this.openLastPeople(player));
            case PLACES -> this.openGo(player);
            case SURVIVAL_HOME -> this.returnSurvivalHome(player);
            case SANDBOX -> this.openDrawing(player);
            case NONE -> {
            }
        }
    }

    private void handlePlacesClick(Player player, int slot) {
        BackendPlacesClickService.PlacesClick<ServerId> placesClick = this.placesClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                this.currentServer,
                ServerId.LOBBY,
                ServerId.SURVIVAL,
                ServerId.CREATIVE);
        switch (placesClick.action()) {
            case BACK -> {
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openCubee(player);
            }
            case RETURN_SPAWN -> this.returnServerSpawn(player);
            case TRAVEL -> this.startTravel(player, placesClick.target());
            case NONE -> {
            }
        }
    }

    private void handlePeoplePageClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
        BackendPeopleClickService.PeoplePageClick peopleClick = this.peopleClickService.peoplePageAction(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                Ui.People.FIND.slot(),
                cubeeHolder == null ? null : cubeeHolder.slotTargets);
        switch (peopleClick.action()) {
            case BACK -> {
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openNextTick(() -> this.openCubee(player));
            }
            case NEXT_PAGE -> this.openNextTick(() -> this.openPeople(player, this.peopleNavigationService.nextLoopPage(pageIndex, this.peopleNavigationService.listPeople(player).size())));
            case OPEN_PLAYER -> {
                Player target = Bukkit.getPlayer((UUID)peopleClick.target());
                if (this.peopleNavigationService.canOpenPlayer(target)) {
                    this.openNextTick(() -> this.openPlayer(player, target, pageIndex));
                } else {
                    this.openNextTick(() -> this.openPeople(player, pageIndex));
                }
            }
            case NONE -> {
            }
        }
    }

    private void handlePlayerPageClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        Player target = cubeeHolder == null ? null : Bukkit.getPlayer((UUID)cubeeHolder.subject);
        int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
        BackendPeopleClickService.PlayerPageAction playerAction = this.peopleClickService.playerPageAction(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                Ui.People.MESSAGE.slot(),
                Ui.People.VISIT.slot(),
                Ui.People.INVITE.slot());
        switch (playerAction) {
            case BACK -> this.openNextTick(() -> this.openPeople(player, pageIndex));
            case MESSAGE -> this.startPrivateNote(player, target);
            case VISIT -> this.createMeetRequestOrReturn(player, target, pageIndex, RequestKind.VISIT);
            case INVITE -> this.createMeetRequestOrReturn(player, target, pageIndex, RequestKind.INVITE);
            case NONE -> {
            }
        }
    }

    private void createMeetRequestOrReturn(Player player, Player target, int pageIndex, RequestKind requestKind) {
        if (!this.peopleActionService.canCreateMeetRequest(player, target)) {
            this.openNextTick(() -> this.openPeople(player, pageIndex));
            return;
        }
        this.createRequest(player, target, requestKind);
    }

    private void handleRequestsClick(Player player, int slot) {
        BackendRequestsClickService.RequestClickAction requestClickAction = this.requestsClickService.action(slot, 12, 13, 14);
        if (requestClickAction == BackendRequestsClickService.RequestClickAction.IGNORE) {
            return;
        }
        if (requestClickAction == BackendRequestsClickService.RequestClickAction.NONE) {
            return;
        }
        CubeeHolder cubeeHolder = this.currentHolder(player);
        BackendMeetRequestService.RequestState<RequestKind> requestState = this.meetRequestService.incoming(player.getUniqueId());
        if (requestState == null || requestState.expired) {
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));
            this.clearRequests(player.getUniqueId());
            return;
        }
        if (!this.requestMatchesHolder(cubeeHolder, requestState)) {
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        switch (requestClickAction) {
            case ACCEPT -> this.acceptRequest(player, requestState);
            case DECLINE -> {
                this.declineRequest(requestState);
                player.closeInventory();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            case IGNORE, NONE -> {
            }
        }
    }

    private void handleAdminRootClick(Player player, int slot) {
        CubeeHolder holder = this.currentHolder(player);
        boolean worldExpanded = holder != null && holder.page == CubeePage.ADMIN && holder.worldExpanded;
        BackendAdminRootClickService.AdminRootAction adminRootAction = this.adminRootClickService.action(
                slot,
                Ui.Care.HOME.slot(),
                Ui.Care.PEOPLE.slot(),
                Ui.Care.REQUESTS.slot(),
                Ui.Care.KEYS.slot(),
                Ui.Care.WORLD.slot(),
                Ui.Care.ATMOSPHERE.slot(),
                Ui.Care.UPKEEP.slot(),
                worldExpanded);
        switch (adminRootAction) {
            case HOME -> {
                this.switchCubeeRoot(player, CubeeRoot.CUBEE);
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openCubee(player);
            }
            case PEOPLE -> this.openNextTick(() -> this.openLastAdminPeople(player));
            case REQUESTS -> this.openAdminRequests(player, 0);
            case KEYS -> this.openAdminKeys(player);
            case TOGGLE_WORLD -> this.toggleCareWorldOptions(player);
            case ATMOSPHERE -> this.openNextTick(() -> this.openAdminAtmosphere(player));
            case UPKEEP -> this.openAdminUpkeep(player);
            case NONE -> {
            }
        }
    }

    private void toggleCareWorldOptions(Player player) {
        CubeeHolder holder = this.currentHolder(player);
        if (holder == null || holder.page != CubeePage.ADMIN) return;
        Inventory inventory = player.getOpenInventory().getTopInventory();
        holder.worldExpanded = !holder.worldExpanded;
        if (holder.worldExpanded) {
            this.renderCareWorldButton(inventory, true);
            this.setButton(inventory, Ui.Care.ATMOSPHERE);
            this.setButtonLore(inventory, Ui.Care.UPKEEP, this.optionalStatusLore("keep the world ready.", this.worldActiveStatus()));
            return;
        }
        inventory.setItem(Ui.Care.ATMOSPHERE.slot(), null);
        inventory.setItem(Ui.Care.UPKEEP.slot(), null);
        this.renderCareWorldButton(inventory, false);
    }

    private void renderCareWorldButton(Inventory inventory, boolean worldExpanded) {
        Ui.ButtonSpec worldButton = this.currentWorldButton();
        this.setButtonLore(inventory, worldButton, worldExpanded
                ? this.worldLore()
                : this.loreLines("keep your world living."));
    }

    private void handleAdminKeysClick(Player player, int slot) {
        BackendAdminKeysClickService.AdminKeysAction adminKeysAction = this.adminKeysClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                12,
                13);
        switch (adminKeysAction) {
            case BACK -> this.openAdmin(player);
            case GIVE -> this.openNextTick(() -> this.openAdminKeyPeople(player, 0));
            case HOLDERS -> this.openNextTick(() -> this.openAdminKeyHolders(player, 0));
            case NONE -> {
            }
        }
    }

    private void handleAdminKeyHoldersClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
        BackendAdminPagedSelectionClickService.SelectionResult clickResult = this.adminPagedSelectionClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                4,
                cubeeHolder == null ? null : cubeeHolder.slotKeys);
        switch (clickResult.action()) {
            case BACK -> this.openNextTick(() -> this.openAdminKeys(player));
            case NEXT_PAGE -> this.openNextTick(() -> this.openAdminKeyHolders(player, this.adminAccessNavigationService.nextLoopPage(pageIndex, this.adminAccessNavigationService.holderNames(player).size())));
            case SELECT -> this.openNextTick(() -> this.openAdminTakeKeyConfirm(player, clickResult.selectedName(), pageIndex));
            case NONE -> {
            }
        }
    }

    private void handleAdminKeyHolderClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
        String accessName = cubeeHolder == null ? null : cubeeHolder.accessName;
        BackendAdminKeyHolderClickService.AdminKeyHolderAction adminKeyHolderAction = this.adminKeyHolderClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                13,
                accessName);
        switch (adminKeyHolderAction) {
            case BACK -> this.openNextTick(() -> this.openAdminKeyHolders(player, pageIndex));
            case TAKE -> this.openNextTick(() -> this.openAdminTakeKeyConfirm(player, accessName, pageIndex));
            case NONE -> {
            }
        }
    }

    private void handleAdminKeyTakeConfirmClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        String accessName = cubeeHolder == null ? null : cubeeHolder.accessName;
        BackendAdminConfirmClickService.ConfirmAction adminKeyTakeConfirmAction = this.adminConfirmClickService.action(
                slot,
                14,
                12,
                accessName != null);
        switch (adminKeyTakeConfirmAction) {
            case CONFIRM -> {
                if (this.setAccessAdmin(player, accessName, false)) {
                    player.closeInventory();
                    this.sendWaitingStatus(player);
                }
            }
            case CANCEL -> {
                player.closeInventory();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminKeyPeopleClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
        BackendAdminPagedSelectionClickService.SelectionResult clickResult = this.adminPagedSelectionClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                4,
                cubeeHolder == null ? null : cubeeHolder.slotKeys);
        switch (clickResult.action()) {
            case BACK -> this.openNextTick(() -> this.openAdminKeys(player));
            case NEXT_PAGE -> this.openNextTick(() -> this.openAdminKeyPeople(player, this.adminAccessNavigationService.nextLoopPage(pageIndex, this.adminAccessNavigationService.candidateNames().size())));
            case SELECT -> this.openNextTick(() -> this.openAdminKeyGive(player, clickResult.selectedName(), pageIndex));
            case NONE -> {
            }
        }
    }

    private void handleAdminKeyGiveClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
        String accessName = cubeeHolder == null ? null : cubeeHolder.accessName;
        BackendAdminConfirmClickService.ConfirmAction adminKeyGiveAction = this.adminConfirmClickService.action(
                slot,
                14,
                12,
                accessName != null);
        switch (adminKeyGiveAction) {
            case CONFIRM -> {
                if (!this.adminAccessActionService.canGive(accessName)) {
                    player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
                    this.openNextTick(() -> this.openAdminKeyPeople(player, pageIndex));
                    return;
                }
                if (this.setAccessAdmin(player, accessName, true)) {
                    player.closeInventory();
                    this.sendWaitingStatus(player);
                }
            }
            case CANCEL -> {
                player.closeInventory();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminSelfClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        int pageIndex = cubeeHolder == null ? -1 : cubeeHolder.pageIndex;
        BackendAdminSelfClickService.AdminSelfAction adminSelfAction = this.adminSelfClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                12,
                13);
        switch (adminSelfAction) {
            case BACK -> {
                if (pageIndex >= 0) {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                } else {
                    this.openNextTick(() -> this.openAdmin(player));
                }
            }
            case GAMEMODE -> {
                if (this.isBusy(player.getUniqueId())) {
                    this.openRecoveryTick(() -> this.openCubee(player));
                    return;
                }
                this.openNextTick(() -> this.openAdminGamemode(player, player, pageIndex));
            }
            case CLEAR -> {
                if (this.isBusy(player.getUniqueId())) {
                    this.openRecoveryTick(() -> this.openCubee(player));
                    return;
                }
                this.openNextTick(() -> this.openCareSelfClearConfirm(player));
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminSelfClearConfirmClick(Player player, int slot) {
        BackendAdminConfirmClickService.ConfirmAction adminSelfClearConfirmAction = this.adminConfirmClickService.action(
                slot,
                14,
                12);
        switch (adminSelfClearConfirmAction) {
            case CONFIRM -> this.clearCareSelfInventory(player);
            case CANCEL -> {
                player.closeInventory();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminPeopleClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
        BackendAdminPeopleClickService.AdminPeopleClick adminPeopleClick = this.adminPeopleClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                4,
                cubeeHolder == null ? null : cubeeHolder.slotTargets);
        switch (adminPeopleClick.action()) {
            case BACK -> {
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openNextTick(() -> this.openAdmin(player));
            }
            case NEXT_PAGE -> this.openNextTick(() -> this.openAdminPeople(player, this.adminPeopleNavigationService.nextLoopPage(pageIndex, this.adminPeopleNavigationService.listPeople(player).size())));
            case SELECT -> {
                UUID targetId = adminPeopleClick.target();
                if (targetId.equals(player.getUniqueId())) {
                    this.openNextTick(() -> this.openCareSelf(player, pageIndex));
                    return;
                }
                Player target = Bukkit.getPlayer((UUID)targetId);
                if (target != null && this.adminPeopleActionService.canTarget(player, target)) {
                    this.openNextTick(() -> this.openAdminPlayer(player, target, pageIndex));
                }
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminRequestsClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        int pageIndex = cubeeHolder == null ? 0 : cubeeHolder.pageIndex;
        BackendAdminRequestsClickService.AdminRequestsClickResult clickResult = this.adminRequestsClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                4,
                cubeeHolder == null ? null : cubeeHolder.slotKeys);
        switch (clickResult.action) {
            case BACK -> this.openNextTick(() -> this.openAdmin(player));
            case NEXT_PAGE -> this.openNextTick(() -> this.openAdminRequests(player, this.adminResetNavigationService.nextLoopPage(pageIndex, this.adminResetNavigationService.tokens().size())));
            case SELECT -> this.openNextTick(() -> this.openAdminReset(player, clickResult.selectedToken, pageIndex));
            case NONE -> {
            }
        }
    }

    private void handleAdminResetClick(Player player, int slot) {
        CubeeHolder cubeeHolder = this.currentHolder(player);
        String token = cubeeHolder == null ? null : cubeeHolder.resetToken;
        if (!this.adminResetActionService.canOpen(token)) {
            this.openNextTick(() -> this.openAdminRequests(player, 0));
            return;
        }
        BackendAdminResetClickService.AdminResetAction adminResetAction = this.adminResetClickService.action(
                slot,
                14,
                12);
        switch (adminResetAction) {
            case ALLOW -> this.allowResetRequest(player, token);
            case DENY -> this.denyResetRequest(player, token);
            case NONE -> {
            }
        }
    }

    private void handleAdminAtmosphereClick(Player player, int slot) {
        BackendAdminAtmosphereClickService.AdminAtmosphereClick adminAtmosphereClick = this.adminAtmosphereClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                this.adminWorldNavigationService);
        switch (adminAtmosphereClick.action()) {
            case BACK -> this.openNextTick(() -> this.openAdmin(player));
            case TIME -> {
                this.setAdminTime(player, adminAtmosphereClick.time());
                player.closeInventory();
            }
            case WEATHER -> {
                this.setAdminWeather(player, adminAtmosphereClick.weather());
                player.closeInventory();
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminUpkeepClick(Player player, int slot) {
        BackendAdminUpkeepClickService.AdminUpkeepAction adminUpkeepAction = this.adminUpkeepClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                12,
                13);
        switch (adminUpkeepAction) {
            case BACK -> this.openAdmin(player);
            case BACKUP -> this.openNextTick(() -> this.openAdminBackupConfirm(player));
            case CHUNKS -> {
                if (this.adminChunkActionService.canOpenChunks(this.chunksAvailableHere())) {
                    this.openNextTick(() -> this.openAdminChunks(player));
                }
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminChunksClick(Player player, int slot) {
        BackendAdminChunksClickService.AdminChunksAction adminChunksAction = this.adminChunksClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                11,
                12,
                13,
                14,
                15);
        switch (adminChunksAction) {
            case BACK -> this.openAdminUpkeep(player);
            case CENTER -> {
                if (this.adminChunkActionService.canEditChunks(this.chunksRunning())) {
                    this.setChunksCenter(player);
                }
            }
            case SIZE -> {
                if (this.adminChunkActionService.canEditChunks(this.chunksRunning())) {
                    this.openNextTick(() -> this.openAdminChunksSize(player));
                }
            }
            case DIMENSION -> {
                if (this.adminChunkActionService.canEditChunks(this.chunksRunning())) {
                    this.openNextTick(() -> this.openAdminChunksDimension(player));
                }
            }
            case CANCEL -> {
                if (this.adminChunkActionService.canCancelChunks(this.chunksRunning())) {
                    this.cancelChunks(player);
                }
            }
            case START -> {
                if (this.adminChunkActionService.canStartChunks(this.chunksRunning())) {
                    this.openNextTick(() -> this.openAdminChunksConfirm(player));
                }
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminChunksDimensionClick(Player player, int slot) {
        BackendAdminChunksDimensionClickService.AdminChunksDimensionClick<ChunkDimension> click = this.adminChunksDimensionClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                this.adminWorldNavigationService,
                ChunkDimension.WORLD,
                ChunkDimension.NETHER,
                ChunkDimension.THE_END);
        switch (click.action()) {
            case BACK -> this.openAdminChunks(player);
            case SELECT -> {
                this.setChunksDimension(click.dimension());
                this.openNextTick(() -> this.openAdminChunks(player));
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminChunksSizeClick(Player player, int slot) {
        BackendAdminChunksSizeClickService.AdminChunksSizeClick click = this.adminChunksSizeClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                this.adminWorldNavigationService);
        switch (click.action()) {
            case BACK -> this.openAdminChunks(player);
            case SELECT -> {
                this.setChunksSize(click.size());
                this.openNextTick(() -> this.openAdminChunks(player));
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminChunksConfirmClick(Player player, int slot) {
        BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(
                slot,
                14,
                12);
        switch (action) {
            case CONFIRM -> this.startChunks(player);
            case CANCEL -> {
                player.closeInventory();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminBackupConfirmClick(Player player, int slot) {
        BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(
                slot,
                14,
                12);
        switch (action) {
            case CONFIRM -> this.startManualBackup(player);
            case CANCEL -> {
                player.closeInventory();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminGamemodeClick(Player player, Player target, int pageIndex, int slot) {
        BackendAdminGamemodeClickService.AdminGamemodeClick click = this.adminGamemodeClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                target.getGameMode());
        switch (click.action()) {
            case BACK -> {
                if (this.adminPlayerControlService.isSelf(player, target)) {
                    this.openNextTick(() -> this.openCareSelf(player, Math.max(0, pageIndex)));
                } else if (this.adminPlayerControlService.isControlPageMarker(pageIndex)) {
                    int controlPageIndex = this.adminPlayerControlService.controlPageIndex(pageIndex);
                    this.openNextTick(() -> this.openAdminPlayerControl(player, target, controlPageIndex));
                } else {
                    this.openNextTick(() -> this.openAdminPlayer(player, target, pageIndex));
                }
            }
            case SELECT -> {
                player.closeInventory();
                this.setAdminGamemode(player, target.getUniqueId(), click.gameMode());
            }
            case CURRENT -> {
            }
            case NONE -> {
            }
        }
    }

    private void handleAdminPlayerClick(Player player, Player target, int pageIndex, int slot) {
        BackendAdminPlayerClickService.AdminPlayerAction action = this.adminPlayerClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                4,
                12,
                13,
                14);
        switch (action) {
            case BACK -> this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
            case CONTROL -> {
                if (this.adminPeopleActionService.canTarget(player, target)) {
                    this.openNextTick(() -> this.openAdminPlayerControl(player, target, pageIndex));
                } else {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
            case VISIT -> {
                if (this.adminPeopleActionService.canTarget(player, target)) {
                    this.adminVisit(player, target);
                } else {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
            case INVITE -> {
                if (this.adminPeopleActionService.canTarget(player, target)) {
                    this.adminInvite(player, target);
                } else {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
            case MESSAGE -> {
                if (!this.adminPeopleActionService.canTarget(player, target)) {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                } else if (this.adminPeopleActionService.canStartMessage(target)) {
                    this.startPrivateNote(player, target);
                }
            }
            case NONE -> {
                if (!this.adminPeopleActionService.canTarget(player, target)) {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
        }
    }

    private void handleAdminPlayerControlClick(Player player, Player target, int pageIndex, int slot) {
        BackendAdminPlayerControlClickService.AdminPlayerControlAction action = this.adminPlayerControlClickService.action(
                slot,
                Ui.Shared.NAV_BACK.slot(),
                12,
                13,
                14);
        switch (action) {
            case BACK -> {
                if (target != null && target.isOnline()) {
                    this.openNextTick(() -> this.openAdminPlayer(player, target, pageIndex));
                } else {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
            case GAMEMODE -> {
                if (this.adminPeopleActionService.canTarget(player, target)) {
                    this.openNextTick(() -> this.openAdminGamemode(player, target, this.adminPlayerControlService.controlPageMarker(pageIndex)));
                } else {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
            case CLEAR -> {
                if (this.adminPeopleActionService.canTarget(player, target)) {
                    this.openNextTick(() -> this.openAdminPlayerClearConfirm(player, target, pageIndex));
                } else {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
            case SEND -> {
                if (this.adminPeopleActionService.canTarget(player, target)) {
                    this.openNextTick(() -> this.openAdminPlayerSendPlaces(player, target, pageIndex));
                } else {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
            case NONE -> {
                if (!this.adminPeopleActionService.canTarget(player, target)) {
                    this.openNextTick(() -> this.openAdminPeople(player, pageIndex));
                }
            }
        }
    }

    private void handleAdminPlayerSendPlacesClick(Player actor, Player target, int pageIndex, int slot) {
        BackendPlacesClickService.PlacesClick<ServerId> click = this.placesClickService.action(
                slot, Ui.Shared.NAV_BACK.slot(), this.currentServer,
                ServerId.LOBBY, ServerId.SURVIVAL, ServerId.CREATIVE);
        switch (click.action()) {
            case BACK -> this.openNextTick(() -> this.openAdminPlayerControl(actor, target, pageIndex));
            case RETURN_SPAWN -> this.sendAdminTargetToCurrentSpawn(actor, target);
            case TRAVEL -> this.startAdminSend(actor, target, click.target());
            case NONE -> {
            }
        }
    }

    private void startAdminSend(Player actor, Player target, ServerId destination) {
        if (actor == null || !actor.isOnline() || !this.requireAdmin(actor) || !this.adminPeopleActionService.canTarget(actor, target) || destination == null) {
            this.notifyAdminSend(actor, "try again", NamedTextColor.DARK_GRAY);
            return;
        }
        AdminSendOperation existing = this.currentAdminSend(actor.getUniqueId());
        if (existing != null) {
            if (existing.targetId.equals(target.getUniqueId()) && existing.destination == destination) {
                this.cancelAdminSend(actor.getUniqueId(), true);
                return;
            }
            this.cancelAdminSend(actor.getUniqueId(), false);
        }
        if (!this.isServerAvailable(destination) && !this.isPlaceWakeable(destination)) {
            this.notifyAdminSend(actor, "out of range", NamedTextColor.DARK_GRAY);
            return;
        }
        BackendOperationToken token = this.nextAdminSendToken();
        AdminSendOperation pending = new AdminSendOperation(actor.getUniqueId(), target.getUniqueId(), destination, token,
                new BackendOperationStatusLease(this.actionBarCoordinator, actor.getUniqueId(), BackendActionBarCoordinator.Owner.ADMIN_SEND));
        if (!this.adminSendOperations.beginIfAbsent(actor.getUniqueId(), token, pending)) {
            pending.statusLease.close();
            this.notifyAdminSend(actor, "try again", NamedTextColor.DARK_GRAY);
            return;
        }
        if (this.isServerAvailable(destination)) {
            this.dispatchAdminSend(pending);
            return;
        }
        pending.statusLease.publish(Component.text("waiting", NamedTextColor.GRAY));
        this.sendWakePlaceRequest(actor, destination);
        this.setPlaceRuntimeStatus(destination, this.restWakingStatus());
        long timeoutAt = this.monotonicMillis() + 120000L;
        BukkitTask wakeTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> {
            if (!this.isCurrentAdminSend(pending)) return;
            Player currentActor = Bukkit.getPlayer((UUID)pending.actorId);
            Player currentTarget = Bukkit.getPlayer((UUID)pending.targetId);
            if (currentActor == null || !currentActor.isOnline() || !this.requireAdmin(currentActor) || !this.adminPeopleActionService.canTarget(currentActor, currentTarget)) {
                this.failAdminSend(pending, "try again");
                return;
            }
            if (this.isServerAvailable(pending.destination)) {
                this.dispatchAdminSend(pending);
                return;
            }
            if (this.monotonicMillis() >= timeoutAt) {
                this.failAdminSend(pending, "out of range");
                return;
            }
            pending.statusLease.publish(Component.text("waiting", NamedTextColor.GRAY));
        }, 1L, 20L);
        pending.taskSlot.replace(wakeTask);
    }

    private void dispatchAdminSend(AdminSendOperation pending) {
        if (!this.isCurrentAdminSend(pending)) return;
        pending.taskSlot.cancel();
        Player actor = Bukkit.getPlayer((UUID)pending.actorId);
        Player target = Bukkit.getPlayer((UUID)pending.targetId);
        if (actor == null || !actor.isOnline() || !this.requireAdmin(actor) || !this.adminPeopleActionService.canTarget(actor, target) || !this.isServerAvailable(pending.destination)) {
            this.failAdminSend(pending, "try again");
            return;
        }
        this.saveIdentityTransfer(target, pending.destination);
        this.adminSendProtocolService.send(actor, pending.targetId, pending.destination.proxyName, pending.token.operationId());
        actor.closeInventory();
        BukkitTask resultTimeoutTask = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            if (this.isCurrentAdminSend(pending)) this.failAdminSend(pending, "try again");
        }, 200L);
        pending.taskSlot.replace(resultTimeoutTask);
    }

    private void sendAdminTargetToCurrentSpawn(Player actor, Player target) {
        AdminSendOperation existing = this.currentAdminSend(actor.getUniqueId());
        if (existing != null) this.cancelAdminSend(actor.getUniqueId(), false);
        if (actor == null || !actor.isOnline() || !this.requireAdmin(actor) || !this.adminPeopleActionService.canTarget(actor, target)) {
            this.notifyAdminSend(actor, "try again", NamedTextColor.DARK_GRAY);
            return;
        }
        Location spawn = this.currentServerSpawnLocation();
        if (spawn == null || !target.teleport(spawn)) {
            this.notifyAdminSend(actor, "try again", NamedTextColor.DARK_GRAY);
            return;
        }
        actor.closeInventory();
        this.notifyAdminSend(actor, "sent", NamedTextColor.GRAY);
    }

    private void finishAdminSendResult(UUID actorId, UUID targetId, String place, UUID operationId, String result) {
        AdminSendOperation pending = this.currentAdminSend(actorId);
        if (pending == null || !pending.token.operationId().equals(operationId) || !pending.targetId.equals(targetId)
                || !pending.destination.proxyName.equalsIgnoreCase(place)) return;
        if (this.adminSendOperations.removeIfCurrent(actorId, pending.token).isEmpty()) return;
        this.cleanupAdminSendOperation(pending, false);
        Player actor = Bukkit.getPlayer((UUID)actorId);
        if (BackendAdminProtocol.SEND_RESULT_SENT.equals(result)) {
            this.notifyAdminSend(actor, "sent", NamedTextColor.GRAY);
            return;
        }
        Player target = Bukkit.getPlayer((UUID)targetId);
        if (target != null) this.clearIdentityTransfer(target);
        this.notifyAdminSend(actor, "try again", NamedTextColor.DARK_GRAY);
    }

    private void failAdminSend(AdminSendOperation pending, String message) {
        if (this.adminSendOperations.removeIfCurrent(pending.actorId, pending.token).isEmpty()) return;
        this.cleanupAdminSendOperation(pending, true);
        this.notifyAdminSend(Bukkit.getPlayer((UUID)pending.actorId), message, NamedTextColor.DARK_GRAY);
    }

    private void cancelAdminSend(UUID actorId, boolean notify) {
        AdminSendOperation pending = this.adminSendOperations.remove(actorId).map(BackendOperationRegistry.Entry::operation).orElse(null);
        if (pending == null) return;
        this.cleanupAdminSendOperation(pending, true);
        if (notify) this.notifyAdminSend(Bukkit.getPlayer((UUID)actorId), "nothing changed", NamedTextColor.DARK_GRAY);
    }

    private boolean isCurrentAdminSend(AdminSendOperation pending) {
        return pending != null && this.adminSendOperations.isCurrent(pending.actorId, pending.token);
    }

    private AdminSendOperation currentAdminSend(UUID actorId) {
        return actorId == null ? null : this.adminSendOperations.current(actorId)
                .map(BackendOperationRegistry.Entry::operation)
                .orElse(null);
    }

    private BackendOperationToken nextAdminSendToken() {
        long generation = this.adminSendGenerationCounter.updateAndGet(value -> value == Long.MAX_VALUE ? 1L : value + 1L);
        return BackendOperationToken.create(generation);
    }

    private void cleanupAdminSendOperation(AdminSendOperation operation, boolean clearTransfer) {
        if (operation == null) return;
        operation.taskSlot.cancel();
        operation.statusLease.close();
        if (!clearTransfer) return;
        Player target = Bukkit.getPlayer(operation.targetId);
        if (target != null) this.clearIdentityTransfer(target);
    }

    private void notifyAdminSend(Player actor, String message, TextColor color) {
        if (actor == null || !actor.isOnline()) return;
        this.notifyActionBar(actor, BackendActionBarCoordinator.Owner.ADMIN_SEND, Component.text(message, color), 3000L);
    }

    private void clearIdentityTransfer(Player target) {
        if (target == null || this.identities == null) return;
        this.reloadIdentities();
        this.identityTransferService.clearTransfer(this.identities, this.identityKey(target));
        this.saveIdentities();
    }

    private void handleAdminPlayerClearConfirmClick(Player player, Player target, int slot) {
        BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(
                slot,
                14,
                12);
        switch (action) {
            case CONFIRM -> this.clearAdminTargetInventory(player, target);
            case CANCEL -> {
                player.closeInventory();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            case NONE -> {
            }
        }
    }

    private void handleSandboxClick(Player player, BackendSandboxInteractionService.SandboxClickAction sandboxClickAction) {
        switch (sandboxClickAction) {
            case BACK -> {
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openCubee(player);
            }
            case UNDO -> this.undoDrawingIfIdle(player);
            case REDO -> this.redoDrawingIfIdle(player);
            case SET -> this.startDrawing(player, DrawingAction.SET);
            case WALL -> this.startDrawing(player, DrawingAction.WALL);
            case FLOOR -> this.startDrawing(player, DrawingAction.FLOOR);
            case CLONE -> this.startDrawing(player, DrawingAction.CLONE);
            case CLEAR -> this.startDrawing(player, DrawingAction.CLEAR);
            case CIRCLE -> this.startDrawing(player, DrawingAction.CIRCLE);
            case REPLACE -> this.startDrawing(player, DrawingAction.REPLACE);
            case FLIP -> this.startDrawing(player, DrawingAction.FLIP);
            case ROTATE -> this.startDrawing(player, DrawingAction.ROTATE);
            case NONE -> this.sendSandboxStatus(player, "nothing changed.", NamedTextColor.DARK_GRAY);
        }
    }

    private void handleSandboxConfirmClick(Player player, int slot, Runnable confirmAction, Runnable cancelAction) {
        BackendSandboxInteractionService.ConfirmAction confirmClickAction = this.sandboxInteractionService.confirmAction(slot, 14, 12);
        switch (confirmClickAction) {
            case CONFIRM -> confirmAction.run();
            case CANCEL -> {
                cancelAction.run();
                player.closeInventory();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            }
            case NONE -> {
            }
        }
    }

    private void startTravel(Player player, ServerId serverId) {
        this.travelStartService.start(player, serverId, this.currentServer);
    }

    private void startWakeTravel(Player player, ServerId serverId) {
        this.wakeTravelService.start(player, serverId, this.restWakingStatus());
    }

    private void sendWakePlaceRequest(Player player, ServerId serverId) {
        if (serverId != ServerId.SURVIVAL && serverId != ServerId.CREATIVE) {
            return;
        }
        this.wakePlaceService.sendWakePlaceRequest(player, serverId.proxyName);
    }

    private List<Component> survivalHomeLore(Player player) {
        Location location = this.survivalHomeLocation(player);
        return location == null ? this.loreLines("not ready yet.") : this.loreLines("return home.", this.blockCoords(location));
    }

    private Location survivalHomeLocation(Player player) {
        if (this.currentServer != ServerId.SURVIVAL || player == null) {
            return null;
        }
        Location location = player.getRespawnLocation();
        if (location == null || location.getWorld() == null) {
            return null;
        }
        if (!this.hasBedNear(location)) {
            return null;
        }
        return location;
    }

    private boolean hasBedNear(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }
        World world = location.getWorld();
        int n = location.getBlockX();
        int n2 = location.getBlockY();
        int n3 = location.getBlockZ();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -2; k <= 2; ++k) {
                    Material material = world.getBlockAt(n + i, n2 + j, n3 + k).getType();
                    if (Tag.BEDS.isTagged(material)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String blockCoords(Location location) {
        if (location == null) {
            return "";
        }
        return location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
    }

    private String facingBlockCoords(Location location) {
        if (location == null) {
            return "";
        }
        return this.cardinalDirection(location) + " " + this.blockCoords(location);
    }

    private String cardinalDirection(Location location) {
        float f = location == null ? 0.0f : location.getYaw();
        float f2 = (f % 360.0f + 360.0f) % 360.0f;
        if (f2 >= 45.0f && f2 < 135.0f) {
            return "W";
        }
        if (f2 >= 135.0f && f2 < 225.0f) {
            return "N";
        }
        if (f2 >= 225.0f && f2 < 315.0f) {
            return "E";
        }
        return "S";
    }


    private void returnSurvivalHome(Player player) {
        this.returnTravelService.returnHome(player, () -> this.survivalHomeLocation(player));
    }

    private void returnServerSpawn(Player player) {
        this.returnTravelService.returnSpawn(player, this::currentServerSpawnLocation);
    }

    private Location currentServerSpawnLocation() {
        World world = Bukkit.getWorld((String)this.placeSpawnWorld(this.currentServer));
        if (world == null) {
            return null;
        }
        return world.getSpawnLocation();
    }

    private void finishProxySkinApply(UUID uUID, String string, String string2) {
        PendingSkinApply pendingSkinApply = this.pendingSkinApplies.remove(uUID);
        if (pendingSkinApply == null) {
            return;
        }
        boolean bl = pendingSkinApply.shouldNotify();
        if (bl && !this.activeSkinChanges.remove(uUID)) {
            return;
        }
        if (bl) {
            this.cancelSkinChangeTimeout(uUID);
        }
        this.skinResultService.handleSkinResult(uUID, string, string2, bl);
    }

    private void startLocalTravel(Player player, Player player2) {
        this.localTravelService.start(player, player2);
    }

    private void adminVisit(Player player, Player player2) {
        if (!this.requireAdmin(player)) {
            return;
        }
        player.closeInventory();
        Location location = player2.getLocation();
        if (!player.teleport(location)) {
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this, () -> player.sendMessage((Component)Component.text((String)(player.isOnline() && this.sameBlockLocation(player.getLocation(), location) ? "done." : "try again."), (TextColor)(player.isOnline() && this.sameBlockLocation(player.getLocation(), location) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))));
    }

    private void adminInvite(Player player, Player player2) {
        if (!this.requireAdmin(player)) {
            return;
        }
        player.closeInventory();
        Location location = player.getLocation();
        if (!player2.teleport(location)) {
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this, () -> player.sendMessage((Component)Component.text((String)(player2.isOnline() && this.sameBlockLocation(player2.getLocation(), location) ? "done." : "try again."), (TextColor)(player2.isOnline() && this.sameBlockLocation(player2.getLocation(), location) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))));
    }

    private void adminVisitIfCurrent(Player player, UUID uUID) {
        Player player2 = this.currentAdminTarget(player, uUID);
        if (player2 == null) {
            return;
        }
        this.adminVisit(player, player2);
    }

    private void adminInviteIfCurrent(Player player, UUID uUID) {
        Player player2 = this.currentAdminTarget(player, uUID);
        if (player2 == null) {
            return;
        }
        this.adminInvite(player, player2);
    }

    private void startPrivateNoteIfCurrent(Player player, UUID uUID) {
        Player player2 = Bukkit.getPlayer((UUID)uUID);
        if (!this.peopleActionService.isCurrentOnline(player2)) {
            player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.startPrivateNote(player, player2);
    }

    private void openAdminPlayerIfCurrent(Player player, UUID uUID, int n) {
        Player player2 = this.currentAdminTarget(player, uUID);
        if (player2 == null) {
            return;
        }
        this.openAdminPlayer(player, player2, n);
    }

    private void openAdminPlayerControlIfCurrent(Player player, UUID uUID, int n) {
        Player player2 = this.currentAdminTarget(player, uUID);
        if (player2 == null) {
            return;
        }
        this.openAdminPlayerControl(player, player2, n);
    }

    private void openAdminGamemodeIfCurrent(Player player, UUID uUID, int n) {
        Player player2 = this.currentAdminTarget(player, uUID);
        if (player2 == null) {
            return;
        }
        this.openAdminGamemode(player, player2, n);
    }

    private int adminControlPageMarker(int n) {
        return this.adminPlayerControlService.controlPageMarker(n);
    }

    private Player currentAdminTarget(Player player, UUID uUID) {
        if (!this.requireAdmin(player)) {
            return null;
        }
        Player player2 = this.adminPeopleActionService.currentTarget(player, uUID);
        if (player2 == null) {
            this.openAdminPeople(player, 0);
            return null;
        }
        return player2;
    }

    private void finishTravel(Player player, ServerId serverId, BackendOperationToken token) {
        this.travelFinishService.finish(player, serverId, token);
    }

    private void cancelTravel(Player player, boolean bl) {
        this.travelStateService.cancel(player, bl);
    }

    private void openPeople(Player player, int n) {
        if (this.routeBusyCubeeState(player)) {
            return;
        }
        this.switchCubeeRoot(player, CubeeRoot.CUBEE);
        this.switchCubeeSurface(player, CubeeSurface.PEOPLE);
        List<Player> list = this.peopleNavigationService.listPeople(player);
        int n3 = this.peopleNavigationService.pageIndex(n, list.size());
        this.rememberPeoplePageIndex(player, n3);
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockPeople(player, n3);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.PEOPLE);
        cubeeHolder.pageIndex = n3;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)"People", (TextColor)HoneyPalette.DEFAULT_WHITE));
        for (BackendPeopleNavigationService.SlotTarget slotTarget : this.peopleNavigationService.pageTargets(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)) {
            Player player2 = slotTarget.player;
            cubeeHolder.slotTargets.put(slotTarget.slot, player2.getUniqueId());
            inventory.setItem(slotTarget.slot, this.playerItem(player2, this.peopleNavigationService.itemStatus(player2)));
        }
        this.setGeneralNav(inventory, true, null);
        if (this.peopleNavigationService.hasAnyNextPage(list.size())) {
            this.setButton(inventory, Ui.People.FIND);
        }
        player.openInventory(inventory);
    }

    private void openPlayer(Player player, Player player2) {
        this.openPlayer(player, player2, 0);
    }

    private void openPlayer(Player player, Player player2, int n) {
        this.rememberPeoplePageIndex(player, n);
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockPlayer(player, player2);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.PLAYER);
        cubeeHolder.subject = player2.getUniqueId();
        cubeeHolder.pageIndex = n;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)player2.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE));
        inventory.setItem(4, this.playerItem(player2, "keep close."));
        this.setButton(inventory, Ui.People.VISIT);
        this.setButton(inventory, Ui.People.INVITE);
        this.setButton(inventory, Ui.People.MESSAGE);
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdmin(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdmin(player);
            return;
        }
        this.switchCubeeSurface(player, CubeeSurface.HOME);
        this.activateCareOperationStatus(player);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN), (int)27, this.careTitleComponent());
        this.setGeneralNav(inventory, false, null);
        this.setButton(inventory, Ui.Care.PEOPLE, this.peopleHereLore(Bukkit.getOnlinePlayers().size()));
        this.setButton(inventory, Ui.Care.REQUESTS, this.resetRequestCountLore());
        this.setButton(inventory, Ui.Care.KEYS);
        this.renderCareWorldButton(inventory, false);
        this.setButton(inventory, Ui.Care.HOME);
        player.openInventory(inventory);
    }

    private void openCareSelf(Player player) {
        this.openCareSelf(player, -1);
    }

    private void openAdminKeys(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminKeys(player);
            return;
        }
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN_KEYS), (int)27, (Component)Component.text((String)"Keys", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.Care.KEY_PEOPLE);
        this.setButtonLore(inventory, Ui.Care.KEY_HOLDERS, this.loreLines("see who keeps the keys.", this.keyHoldersStatus()));
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminKeyHolders(Player player, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminKeyHolders(player);
            return;
        }
        List<String> list = this.adminAccessNavigationService.holderNames(player);
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_KEY_HOLDERS);
        cubeeHolder.pageIndex = this.adminAccessNavigationService.pageIndex(n, list.size());
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)this.subpageTitle("Keys", "Holders"));
        for (BackendAdminAccessNavigationService.SlotKey slotKey : this.adminAccessNavigationService.pageKeys(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)) {
            cubeeHolder.slotKeys.put(slotKey.slot, slotKey.name);
            inventory.setItem(slotKey.slot, this.menuItem(Material.PLAYER_HEAD, slotKey.name, null));
        }
        if (this.adminAccessNavigationService.hasAnyNextPage(list.size())) {
            this.setButton(inventory, Ui.Care.KEY_FIND);
        }
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminTakeKeyConfirm(Player player, String string, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminAccessActionService.isHolder(string)) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            this.openAdminKeyHolders(player, n);
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminTakeKeyConfirm(player, string);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_KEY_TAKE_CONFIRM);
        cubeeHolder.accessName = string;
        cubeeHolder.pageIndex = n;
        boolean bl = this.adminAccessActionService.isSelfHolder(player, string);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)(bl ? "Leave the keys?" : "Take their key?"), (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, bl ? Ui.Care.STEP_AWAY_CONFIRM : Ui.Care.TAKE_KEY_CONFIRM);
        inventory.setItem(13, this.menuItem(Material.PLAYER_HEAD, string, null));
        this.setButton(inventory, Ui.CareSelf.CANCEL);
        player.openInventory(inventory);
    }

    private void openAdminKeyPeople(Player player, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminKeyPeople(player);
            return;
        }
        List<String> list = this.adminAccessNavigationService.candidateNames();
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_KEY_PEOPLE);
        cubeeHolder.pageIndex = this.adminAccessNavigationService.pageIndex(n, list.size());
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)this.subpageTitle("Keys", "Give"));
        for (BackendAdminAccessNavigationService.SlotKey slotKey : this.adminAccessNavigationService.pageKeys(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)) {
            cubeeHolder.slotKeys.put(slotKey.slot, slotKey.name);
            inventory.setItem(slotKey.slot, this.menuItem(Material.PLAYER_HEAD, slotKey.name, null));
        }
        if (this.adminAccessNavigationService.hasAnyNextPage(list.size())) {
            this.setButton(inventory, Ui.Care.FIND);
        }
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminKeyGive(Player player, String string, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        String string2 = this.adminAccessActionService.normalize(string);
        if (!this.adminAccessActionService.canGive(string2)) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            this.openAdminKeyPeople(player, n);
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminKeyGive(player, string2);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_KEY_GIVE);
        cubeeHolder.accessName = string2;
        cubeeHolder.pageIndex = n;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)"Let them care?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.CareSelf.CANCEL);
        inventory.setItem(13, this.menuItem(Material.PLAYER_HEAD, string2, null));
        this.setButton(inventory, Ui.Care.CARE_KEY);
        player.openInventory(inventory);
    }

    private void openCareSelf(Player player, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (n >= 0) {
            this.rememberAdminPeoplePageIndex(player, n);
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockCareSelf(player, n);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_SELF);
        cubeeHolder.pageIndex = n;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)player.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, new Ui.ButtonSpec(Ui.CareSelf.GAMEMODE.slot(), this.gamemodeMaterial(player.getGameMode()), Ui.CareSelf.GAMEMODE.title(), Ui.CareSelf.GAMEMODE.lore()));
        this.setButton(inventory, Ui.CareSelf.CLEAR);
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openCareSelfClearConfirm(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_SELF_CLEAR_CONFIRM);
        cubeeHolder.pageIndex = Math.max(-1, this.currentHolder(player) == null ? -1 : this.currentHolder((Player)player).pageIndex);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)"Clear your things?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.CareSelf.CLEAR_CONFIRM);
        this.setButton(inventory, Ui.CareSelf.SELF_PREVIEW);
        this.setButton(inventory, Ui.CareSelf.CANCEL);
        player.openInventory(inventory);
    }

    private void openAdminPeople(Player player, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        this.switchCubeeRoot(player, CubeeRoot.CARE);
        this.switchCubeeSurface(player, CubeeSurface.ADMIN_PEOPLE);
        List<Player> list = this.adminPeopleNavigationService.listPeople(player);
        int n3 = this.adminPeopleNavigationService.pageIndex(n, list.size());
        this.rememberAdminPeoplePageIndex(player, n3);
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminPeople(player);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_PEOPLE);
        cubeeHolder.pageIndex = n3;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)this.subpageTitle("Care", "People"));
        for (BackendAdminPeopleNavigationService.SlotTarget slotTarget : this.adminPeopleNavigationService.pageTargets(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)) {
            Player player2 = slotTarget.player;
            cubeeHolder.slotTargets.put(slotTarget.slot, player2.getUniqueId());
            inventory.setItem(slotTarget.slot, this.playerItem(player2, this.adminPeopleNavigationService.inventoryStatus(player, player2)));
        }
        this.setGeneralNav(inventory, true, null);
        if (this.adminPeopleNavigationService.hasAnyNextPage(list.size())) {
            this.setButton(inventory, Ui.Care.FIND);
        }
        player.openInventory(inventory);
    }

    private void openAdminPlayer(Player player, Player player2, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        this.rememberAdminPeoplePageIndex(player, n);
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminPlayer(player, player2);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_PLAYER);
        cubeeHolder.subject = player2.getUniqueId();
        cubeeHolder.pageIndex = n;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)player2.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE));
        inventory.setItem(4, this.playerItem(player2, "look after them."));
        this.setButton(inventory, Ui.CarePlayer.VISIT);
        this.setButton(inventory, Ui.CarePlayer.INVITE);
        this.setButton(inventory, Ui.CarePlayer.MESSAGE, this.adminPeopleActionService.messageLore(player2, Ui.CarePlayer.MESSAGE.lore()));
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminPlayerControl(Player player, Player player2, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminPeopleActionService.canTarget(player, player2)) {
            this.openAdminPeople(player, n);
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminPlayerControl(player, player2, n);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_PLAYER_CONTROL);
        cubeeHolder.subject = player2.getUniqueId();
        cubeeHolder.pageIndex = n;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)player2.getName(), (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, new Ui.ButtonSpec(Ui.CareSelf.GAMEMODE.slot(), this.gamemodeMaterial(player2.getGameMode()), Ui.CareSelf.GAMEMODE.title(), "choose their mode."));
        this.setButton(inventory, Ui.CareSelf.TARGET_CLEAR);
        this.setButton(inventory, Ui.CarePlayer.SEND);
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminPlayerClearConfirm(Player player, Player player2, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminPeopleActionService.canTarget(player, player2)) {
            this.openAdminPeople(player, n);
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminPlayerClearConfirm(player, player2, n);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_PLAYER_CLEAR_CONFIRM);
        cubeeHolder.subject = player2.getUniqueId();
        cubeeHolder.pageIndex = n;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)"Clear their things?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.CareSelf.TARGET_CLEAR_CONFIRM);
        inventory.setItem(13, this.playerItem(player2, null));
        this.setButton(inventory, Ui.CareSelf.CANCEL);
        player.openInventory(inventory);
    }

    private void openAdminRequests(Player player, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminRequests(player);
            return;
        }
        List<String> list = this.adminResetNavigationService.tokens();
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_REQUESTS);
        cubeeHolder.pageIndex = this.adminResetNavigationService.pageIndex(n, list.size());
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)this.subpageTitle("Care", "Requests"));
        for (BackendAdminResetNavigationService.SlotToken slotToken : this.adminResetNavigationService.pageTokens(list, cubeeHolder.pageIndex, PEOPLE_SLOTS)) {
            cubeeHolder.slotKeys.put(slotToken.slot, slotToken.token);
            String string2 = this.adminResetNavigationService.displayName(slotToken.token);
            inventory.setItem(slotToken.slot, this.menuItem(Material.PLAYER_HEAD, string2, this.adminResetNavigationService.requestLore(string2)));
        }
        this.setGeneralNav(inventory, true, null);
        if (this.adminResetNavigationService.hasAnyNextPage(list.size())) {
            this.setButton(inventory, Ui.Care.REVIEW);
        }
        player.openInventory(inventory);
    }

    private void openAdminReset(Player player, String string, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminResetActionService.canOpen(string)) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            this.openAdminRequests(player, n);
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminReset(player, string);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_RESET);
        cubeeHolder.resetToken = string;
        cubeeHolder.pageIndex = n;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)"Let them reset?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        String string2 = this.adminResetNavigationService.displayName(string);
        this.setButton(inventory, Ui.Reset.ALLOW);
        inventory.setItem(13, this.menuItem(Material.PLAYER_HEAD, string2, null));
        this.setButton(inventory, Ui.Reset.NOT_NOW);
        player.openInventory(inventory);
    }

    private void openAdminAtmosphere(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminAtmosphere(player);
            return;
        }
        this.activateCareOperationStatus(player);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN_ATMOSPHERE), (int)27, (Component)this.subpageTitle("World", "Atmosphere"));
        this.setButton(inventory, Ui.Atmosphere.DAY);
        this.setButton(inventory, Ui.Atmosphere.NIGHT);
        this.setButton(inventory, Ui.Atmosphere.RAIN);
        this.setButton(inventory, Ui.Atmosphere.CLEAR);
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminUpkeep(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdmin(player);
            return;
        }
        this.activateCareOperationStatus(player);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN_UPKEEP), (int)27, (Component)this.subpageTitle("World", "Upkeep"));
        if (this.adminChunkActionService.canOpenChunks(this.chunksAvailableHere())) {
            this.setButtonLore(inventory, Ui.Care.CHUNKS, this.chunksLore());
        }
        this.setButtonLore(inventory, Ui.Care.BACKUP, this.backupLore());
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminChunks(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminChunkActionService.canOpenChunks(this.chunksAvailableHere())) {
            player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdmin(player);
            return;
        }
        this.activateCareOperationStatus(player);
        ChunkDimension chunkDimension = this.selectedChunkDimension();
        int n = this.selectedChunkSize();
        boolean bl = this.chunksRunning();
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN_CHUNKS), (int)27, (Component)this.statusTitle("Chunks", this.chunkStatus()));
        this.setButton(inventory, Ui.Care.CHUNKS_START, this.adminChunkActionService.chunksButtonLore(bl));
        this.setButton(inventory, Ui.Care.CHUNKS_CANCEL);
        String string = this.chunkActiveStatus();
        if (string != null) {
            this.setItemLore(inventory.getItem(Ui.Care.CHUNKS_CANCEL.slot()), this.loreLines("stop preparing.", string));
        }
        this.setButton(inventory, new Ui.ButtonSpec(13, chunkDimension.material, "Dimension", this.adminChunkActionService.dimensionLore(bl)));
        if (!bl) {
            this.setItemLore(inventory.getItem(13), List.of(Component.text((String)"choose where.", (TextColor)NamedTextColor.GRAY), Component.text((String)chunkDimension.lore, (TextColor)NamedTextColor.GRAY)));
        }
        this.setButton(inventory, Ui.Care.CHUNKS_SIZE, this.adminChunkActionService.sizeLore(bl));
        if (!bl) {
            this.setItemLore(inventory.getItem(Ui.Care.CHUNKS_SIZE.slot()), List.of(Component.text((String)"choose how far.", (TextColor)NamedTextColor.GRAY), Component.text((String)(n + "."), (TextColor)NamedTextColor.GRAY)));
        }
        this.setButton(inventory, Ui.Care.CHUNKS_CENTER, this.adminChunkActionService.centerLore(bl));
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminChunksDimension(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdmin(player);
            return;
        }
        this.activateCareOperationStatus(player);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN_CHUNKS_DIMENSION), (int)27, (Component)this.subpageTitle("Chunks", "Dimension"));
        this.setButton(inventory, Ui.Chunks.WORLD);
        this.setButton(inventory, Ui.Chunks.NETHER);
        this.setButton(inventory, Ui.Chunks.THE_END);
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminChunksSize(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdmin(player);
            return;
        }
        this.activateCareOperationStatus(player);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN_CHUNKS_SIZE), (int)27, (Component)this.subpageTitle("Chunks", "Size"));
        this.setButton(inventory, Ui.Chunks.SIZE_1500);
        this.setButton(inventory, Ui.Chunks.SIZE_3000);
        this.setButton(inventory, Ui.Chunks.SIZE_5000);
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openAdminChunksConfirm(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdmin(player);
            return;
        }
        this.activateCareOperationStatus(player);
        ChunkDimension chunkDimension = this.selectedChunkDimension();
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN_CHUNKS_CONFIRM), (int)27, (Component)Component.text((String)"Prepare the world?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.Care.CHUNKS_CONFIRM);
        inventory.setItem(13, this.menuItem(chunkDimension.material, "Chunks", null));
        this.setButton(inventory, Ui.CareSelf.CANCEL);
        player.openInventory(inventory);
    }

    private void openAdminBackupConfirm(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdmin(player);
            return;
        }
        this.activateCareOperationStatus(player);
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ADMIN_BACKUP_CONFIRM), (int)27, (Component)Component.text((String)"Keep a copy?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.Care.BACKUP_CONFIRM);
        inventory.setItem(13, this.menuItem(this.placeTargetSpec(13, this.currentServer).material(), this.currentServer.label, null));
        this.setButton(inventory, Ui.CareSelf.CANCEL);
        player.openInventory(inventory);
    }

    private void openAdminGamemode(Player player) {
        this.openAdminGamemode(player, player, 0);
    }

    private void openAdminGamemode(Player player, Player player2, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminPlayerControlService.canOpenGamemode(player2)) {
            this.openAdminPeople(player, n);
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminGamemode(player, player2, n);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.ADMIN_GAMEMODE);
        cubeeHolder.subject = player2.getUniqueId();
        cubeeHolder.pageIndex = n;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)"Mode", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setGamemodeButton(inventory, Ui.Gamemode.ADVENTURE, player2, GameMode.ADVENTURE);
        this.setGamemodeButton(inventory, Ui.Gamemode.SURVIVAL, player2, GameMode.SURVIVAL);
        this.setGamemodeButton(inventory, Ui.Gamemode.CREATIVE, player2, GameMode.CREATIVE);
        this.setGamemodeButton(inventory, Ui.Gamemode.SPECTATOR, player2, GameMode.SPECTATOR);
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openRequests(Player player) {
        BackendMeetRequestService.RequestState<RequestKind> requestState = this.meetRequestService.incoming(player.getUniqueId());
        if (requestState == null || requestState.expired) {
            player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));
            this.clearRequests(player.getUniqueId());
            return;
        }
        Player player2 = Bukkit.getPlayer((UUID)requestState.sender);
        if (player2 == null) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            this.meetRequestService.clear(requestState);
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockRequests(player, requestState, player2);
            return;
        }
        CubeeHolder cubeeHolder = new CubeeHolder(CubeePage.REQUESTS);
        cubeeHolder.requestSender = requestState.sender;
        cubeeHolder.requestKind = requestState.kind;
        if (requestState.kind == RequestKind.VISIT) {
            Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)"Let them visit?", (TextColor)HoneyPalette.DEFAULT_WHITE));
            this.setButton(inventory, Ui.Requests.SURE_VISIT);
            inventory.setItem(13, this.playerItem(player2, null));
            this.setButton(inventory, Ui.Requests.LATER);
            player.openInventory(inventory);
        } else if (requestState.kind == RequestKind.INVITE) {
            Inventory inventory = Bukkit.createInventory((InventoryHolder)cubeeHolder, (int)27, (Component)Component.text((String)"Go with them?", (TextColor)HoneyPalette.DEFAULT_WHITE));
            this.setButton(inventory, Ui.Requests.SURE_INVITE);
            inventory.setItem(13, this.playerItem(player2, null));
            this.setButton(inventory, Ui.Requests.LATER);
            player.openInventory(inventory);
        }
    }

    private List<String> networkOnlineNames() {
        File file = this.runtimeLayout.dataFile("online.yml");
        return this.onlineNamesService.onlineNames(file.toPath(), System.currentTimeMillis());
    }

    private List<String> accessHolderNames() {
        File file = this.runtimeLayout.dataFile("access.yml");
        return this.accessMetadataService.accessHolderNames(file.toPath());
    }

    private boolean isAccessHolder(String string) {
        return this.adminAccessActionService.isHolder(string);
    }

    private void syncAccessState(Player player) {
        if (player == null) {
            return;
        }
        if (this.isAccessHolder(player.getName())) {
            this.updateProxyAdmin(player, true);
        } else {
            this.updateProxyAdmin(player, false);
        }
    }

    private void updateProxyAdmin(Player player, boolean bl) {
        if (bl) {
            this.proxyAdmins.add(player.getUniqueId());
        } else {
            this.proxyAdmins.remove(player.getUniqueId());
        }
        player.updateCommands();
    }

    private String normalizeAccessName(String string) {
        return string == null ? "" : string.trim().toLowerCase(Locale.ROOT);
    }

    private boolean setAccessAdmin(Player player, String string, boolean bl) {
        if (!this.requireAdmin(player)) {
            return false;
        }
        String string2 = this.normalizeAccessName(string);
        if (!this.safeAdminName(string2)) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            return false;
        }
        return this.accessLegacyService.sendAccessRequest(player, string2, bl);
    }

    private List<Component> placesLore() {
        ArrayList<Component> arrayList = new ArrayList<Component>();
        for (String string : this.placesLoreText()) {
            arrayList.add((Component)Component.text((String)string, (TextColor)NamedTextColor.GRAY));
        }
        return arrayList;
    }

    private List<Component> worldLore() {
        ArrayList<Component> arrayList = new ArrayList<Component>();
        arrayList.addAll(this.placesLore());
        return arrayList;
    }

    private List<Component> loreLines(String ... stringArray) {
        ArrayList<Component> arrayList = new ArrayList<Component>();
        for (String string : stringArray) {
            if (string == null || string.isBlank()) continue;
            arrayList.add((Component)Component.text((String)string, (TextColor)NamedTextColor.GRAY));
        }
        return arrayList;
    }

    private List<Component> optionalStatusLore(String string, String string2) {
        return string2 == null || string2.isBlank() ? this.loreLines(string) : this.loreLines(string, string2);
    }

    private String homePeopleStatus(Player player) {
        int n = this.peopleNavigationService.listPeople(player).size();
        return n == 0 ? null : this.peopleHereLore(n);
    }

    private String keyHoldersStatus() {
        int n = this.accessHolderNames().size();
        if (n == 0) {
            return "no holders.";
        }
        return n + (n == 1 ? " holder." : " holders.");
    }

    private String worldActiveStatus() {
        if (this.backupInProgress(this.currentServer)) {
            return "waiting.";
        }
        return this.chunkActiveStatus();
    }

    private String chunkActiveStatus() {
        String string = this.chunkStatus();
        if (this.adminChunkActionService.isActiveStatus(string)) {
            return string;
        }
        return null;
    }

    private Ui.ButtonSpec currentWorldButton() {
        Ui.ButtonSpec buttonSpec = this.placeTargetSpec(Ui.Care.WORLD.slot(), this.currentServer);
        return new Ui.ButtonSpec(Ui.Care.WORLD.slot(), buttonSpec.material(), "World", Text.Lore.WORLD);
    }

    private List<Component> backupLore() {
        ArrayList<Component> arrayList = new ArrayList<Component>();
        arrayList.add((Component)Component.text((String)"keep a copy.", (TextColor)NamedTextColor.GRAY));
        String string = this.lastBackupTimestamp(this.currentServer);
        arrayList.add((Component)Component.text((String)(string == null ? "no copy yet." : "last copy: " + string + "."), (TextColor)NamedTextColor.GRAY));
        return arrayList;
    }

    private List<Component> chunksLore() {
        String string = this.chunkActiveStatus();
        return string == null ? this.loreLines("prepare the world.") : this.loreLines("prepare the world.", string);
    }

    private boolean chunksAvailableHere() {
        return this.currentServer == ServerId.SURVIVAL || this.currentServer == ServerId.CREATIVE;
    }

    private boolean chunkyReady() {
        return Bukkit.getPluginManager().isPluginEnabled("Chunky");
    }

    private Object chunkyApi() {
        if (!this.chunkyReady()) {
            this.chunkyApi = null;
            return null;
        }
        if (this.chunkyApi != null) {
            return this.chunkyApi;
        }
        try {
            Class<?> clazz = Class.forName("org.popcraft.chunky.ChunkyProvider");
            Object object = clazz.getMethod("get", new Class[0]).invoke(null, new Object[0]);
            Class<?> clazz2 = Class.forName("org.popcraft.chunky.api.ChunkyAPIImpl");
            this.chunkyApi = clazz2.getConstructor(object.getClass()).newInstance(object);
            return this.chunkyApi;
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            this.chunkyApi = null;
            return null;
        }
    }

    private ChunkDimension selectedChunkDimension() {
        this.reloadChunks();
        if (this.chunks == null) {
            return ChunkDimension.WORLD;
        }
        return ChunkDimension.fromKey(this.chunkSettingsService.dimension(this.chunks, this.currentServer.proxyName, ChunkDimension.WORLD.key));
    }

    private int selectedChunkSize() {
        this.reloadChunks();
        if (this.chunks == null) {
            return 3000;
        }
        int n = this.chunkSettingsService.size(this.chunks, this.currentServer.proxyName, 3000);
        return switch (n) {
            case 1500, 3000, 5000 -> n;
            default -> 3000;
        };
    }

    private String chunkStatus() {
        this.reloadChunks();
        return this.chunkSettingsService.status(this.chunks, this.currentServer.proxyName);
    }

    private boolean chunksRunning() {
        ChunkDimension chunkDimension = this.selectedChunkDimension();
        Object object = this.chunkyApi();
        if (object != null) {
            String string;
            boolean bl = this.chunkyRunning(object, chunkDimension.bukkitWorldName);
            if (!bl && this.adminChunkActionService.isActiveStatus(string = this.chunkStatus())) {
                this.setChunkStatus("idle.");
            }
            return bl;
        }
        String string = this.chunkStatus();
        return this.adminChunkActionService.isActiveStatus(string);
    }

    private void setChunkStatus(String string) {
        if (this.chunks == null) {
            this.loadChunks();
        }
        this.chunkSettingsService.setStatus(this.chunks, this.currentServer.proxyName, string);
        this.saveChunks();
    }

    private void setChunkStatusForWorld(String string, String string2) {
        ChunkDimension chunkDimension = this.chunkDimensionForWorld(string);
        if (chunkDimension == null) {
            return;
        }
        if (this.chunks == null) {
            this.loadChunks();
        }
        this.chunkSettingsService.setStatusAndDimension(this.chunks, this.currentServer.proxyName, string2, chunkDimension.key);
        this.saveChunks();
    }

    private void setChunksDimension(ChunkDimension chunkDimension) {
        if (this.chunks == null) {
            this.loadChunks();
        }
        this.chunkSettingsService.setDimension(this.chunks, this.currentServer.proxyName, chunkDimension.key);
        this.saveChunks();
    }

    private void setChunksSize(int n) {
        if (this.chunks == null) {
            this.loadChunks();
        }
        this.chunkSettingsService.setSize(this.chunks, this.currentServer.proxyName, n);
        this.saveChunks();
    }

    private void setChunksCenter(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.chunksAvailableHere()) {
            player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        Location location = player.getLocation();
        ChunkDimension chunkDimension = this.chunkDimensionForWorld(location.getWorld() == null ? null : location.getWorld().getName());
        if (chunkDimension == null) {
            chunkDimension = this.selectedChunkDimension();
        }
        if (this.chunks == null) {
            this.loadChunks();
        }
        this.chunkSettingsService.setDimension(this.chunks, this.currentServer.proxyName, chunkDimension.key);
        this.chunkSettingsService.setCenter(this.chunks, this.currentServer.proxyName, chunkDimension.key, location.getBlockX(), location.getBlockZ());
        this.saveChunks();
        this.openNextTick(() -> this.openAdminChunks(player));
    }

    private Location chunkCenter(ChunkDimension chunkDimension) {
        this.reloadChunks();
        if (this.chunkSettingsService.hasCenter(this.chunks, this.currentServer.proxyName, chunkDimension.key)) {
            return new Location(this.chunkWorld(chunkDimension), (double)this.chunkSettingsService.centerX(this.chunks, this.currentServer.proxyName, chunkDimension.key), 0.0, (double)this.chunkSettingsService.centerZ(this.chunks, this.currentServer.proxyName, chunkDimension.key));
        }
        World world = this.chunkWorld(chunkDimension);
        Location location = world == null ? ((World)Bukkit.getWorlds().get(0)).getSpawnLocation() : world.getSpawnLocation();
        return new Location(world, (double)location.getBlockX(), 0.0, (double)location.getBlockZ());
    }

    private World chunkWorld(ChunkDimension chunkDimension) {
        World world = Bukkit.getWorld((String)chunkDimension.bukkitWorldName);
        return world == null ? Bukkit.getWorld((String)"world") : world;
    }

    private ChunkDimension chunkDimensionForWorld(String string) {
        for (ChunkDimension chunkDimension : ChunkDimension.values()) {
            if (!chunkDimension.bukkitWorldName.equalsIgnoreCase(String.valueOf(string))) continue;
            return chunkDimension;
        }
        return null;
    }

    private void registerChunkyListeners() {
        if (!this.chunksAvailableHere()) {
            return;
        }
        Object object2 = this.chunkyApi();
        if (object2 == null) {
            return;
        }
        try {
            Consumer<Object> consumer = object -> Bukkit.getScheduler().runTask((Plugin)this, () -> this.handleChunkyProgress(object));
            Consumer<Object> consumer2 = object -> Bukkit.getScheduler().runTask((Plugin)this, () -> this.handleChunkyComplete(object));
            object2.getClass().getMethod("onGenerationProgress", Consumer.class).invoke(object2, consumer);
            object2.getClass().getMethod("onGenerationComplete", Consumer.class).invoke(object2, consumer2);
            this.reconcileChunkStatus();
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            this.logOperationFailure("chunky-progress", "LemonOS recovered a Chunky progress callback: " + exception.getMessage());
        }
    }

    private void reconcileChunkStatus() {
        Object object = this.chunkyApi();
        if (object == null || this.chunks == null) {
            return;
        }
        boolean bl = false;
        for (ChunkDimension chunkDimension : ChunkDimension.values()) {
            if (!this.chunkyRunning(object, chunkDimension.bukkitWorldName)) continue;
            bl = true;
            this.chunkSettingsService.setDimension(this.chunks, this.currentServer.proxyName, chunkDimension.key);
            String string = this.chunkStatus();
            if (string.endsWith("% ready")) break;
            this.chunkSettingsService.setStatus(this.chunks, this.currentServer.proxyName, "running.");
            break;
        }
        String string = this.chunkStatus();
        if (!bl && this.adminChunkActionService.isActiveStatus(string)) {
            this.chunkSettingsService.setStatus(this.chunks, this.currentServer.proxyName, "idle.");
        }
        this.saveChunks();
    }

    private void handleChunkyProgress(Object object) {
        try {
            float f;
            Boolean bl;
            String string = String.valueOf(object.getClass().getMethod("world", new Class[0]).invoke(object, new Object[0]));
            Object object2 = object.getClass().getMethod("progress", new Class[0]).invoke(object, new Object[0]);
            Object object3 = object.getClass().getMethod("complete", new Class[0]).invoke(object, new Object[0]);
            boolean bl2 = object3 instanceof Boolean && (bl = (Boolean)object3) != false;
            boolean bl3 = bl2;
            if (bl2) {
                this.signalChunkCompletion(string);
                return;
            }
            if (object2 instanceof Number) {
                Number number = (Number)object2;
                f = number.floatValue();
            } else {
                f = 0.0f;
            }
            float f2 = f;
            int n = Math.max(0, Math.min(100, Math.round(f2 <= 1.0f ? f2 * 100.0f : f2)));
            this.setChunkStatusForWorld(string, n + "% ready");
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            this.logOperationFailure("chunky-complete", "LemonOS recovered a Chunky completion callback: " + exception.getMessage());
        }
    }

    private void handleChunkyComplete(Object object) {
        try {
            String string = String.valueOf(object.getClass().getMethod("world", new Class[0]).invoke(object, new Object[0]));
            this.signalChunkCompletion(string);
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            // empty catch block
        }
    }

    private void signalChunkCompletion(String world) {
        ChunkOperation operation = this.currentChunkOperation(world);
        if (operation == null) return;
        boolean[] scheduled = new boolean[1];
        this.chunkOperations.useIfCurrent(world, operation.token, current -> {
            if (current.completionVerificationActive) return;
            current.completionVerificationActive = true;
            scheduled[0] = true;
        });
        if (!scheduled[0]) return;
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.verifyChunkCompletion(world, operation.token, 1));
    }

    private void verifyChunkCompletion(String world, BackendOperationToken token, int attempt) {
        if (!this.chunkOperations.isCurrent(world, token)) return;
        Boolean running = this.chunkyRunningState(this.chunkyApi, world);
        if (running == null) {
            this.chunkOperations.useIfCurrent(world, token, operation -> operation.completionVerificationActive = false);
            this.logOperationFailure("chunky-complete-" + world, "LemonOS could not verify Chunky completion for " + world);
            return;
        }
        if (running) {
            if (attempt < CHUNK_COMPLETION_VERIFY_ATTEMPTS) {
                Bukkit.getScheduler().runTask((Plugin)this, () -> this.verifyChunkCompletion(world, token, attempt + 1));
            } else {
                this.chunkOperations.useIfCurrent(world, token, operation -> operation.completionVerificationActive = false);
            }
            return;
        }
        this.completeChunkOperation(world, token, true, null);
    }

    private boolean chunkyRunning(Object object, String string) {
        return Boolean.TRUE.equals(this.chunkyRunningState(object, string));
    }

    private Boolean chunkyRunningState(Object object, String string) {
        if (object == null) {
            return false;
        }
        try {
            Boolean bl;
            Object object2 = object.getClass().getMethod("isRunning", String.class).invoke(object, string);
            return object2 instanceof Boolean ? (bl = (Boolean)object2) : null;
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            return null;
        }
    }

    private void startPendingOperationWatchdog() {
        this.pendingOperationWatchdogTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> {
            try {
                this.checkActionBarRenderer();
                this.checkChunkActors();
            }
            catch (RuntimeException exception) {
                this.getLogger().warning("LemonOS pending-operation watchdog recovered: " + exception.getMessage());
            }
        }, 40L, 40L);
    }

    private void checkActionBarRenderer() {
        long now = System.nanoTime();
        if (this.actionBarRenderTask != null && !this.actionBarRenderTask.isCancelled()
                && now - this.actionBarHeartbeatNanos <= 5_000_000_000L) return;
        if (this.actionBarRenderTask != null) this.actionBarRenderTask.cancel();
        this.getLogger().warning("LemonOS restarting a stalled Action Bar renderer");
        this.startActionBarRenderTask();
    }

    private void checkChunkActors() {
        for (BackendOperationRegistry.Entry<ChunkOperation> entry : this.chunkOperations.snapshot()) {
            ChunkOperation operation = entry.operation();
            String world = operation.world;
            Boolean running = this.chunkyRunningState(this.chunkyApi, world);
            if (running == null) {
                int failures = ++operation.probeFailures;
                this.logOperationFailure("chunky-probe-" + world, "LemonOS could not read Chunky state for " + world);
                if (failures >= CHUNK_PROBE_FAILURE_LIMIT) this.completeChunkOperation(world, operation.token, false, "state probe failed");
                continue;
            }
            operation.probeFailures = 0;
            if (running) {
                operation.stoppedConfirmations = 0;
                continue;
            }
            int confirmations = ++operation.stoppedConfirmations;
            if (confirmations < CHUNK_STOPPED_CONFIRMATIONS) continue;
            this.completeChunkOperation(world, operation.token, false, "completion callback was not received");
        }
    }

    private void completeChunkOperation(String world, BackendOperationToken token, boolean succeeded, String reason) {
        ChunkOperation operation = this.chunkOperations.removeIfCurrent(world, token)
                .map(BackendOperationRegistry.Entry::operation).orElse(null);
        if (operation == null) return;
        this.setChunkStatusForWorld(world, "idle.");
        if (!succeeded) this.getLogger().warning("LemonOS recovered a Chunky operation for " + world + ": " + reason);
        Player player = Bukkit.getPlayer(operation.actor);
        if (player != null && player.isOnline()) {
            player.sendMessage(Component.text(succeeded ? "done." : "try again.", succeeded ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY));
        }
    }

    private void logOperationFailure(String key, String message) {
        long now = System.nanoTime();
        long last = this.operationErrorLogNanos.getOrDefault(key, 0L);
        if (last != 0L && now - last < 60_000_000_000L) return;
        this.operationErrorLogNanos.put(key, now);
        this.getLogger().warning(message);
    }

    private void startChunks(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminChunkActionService.hasChunkSignal(this.chunksAvailableHere(), this.chunkyReady())) {
            this.setChunkStatus("idle.");
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"no signal.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        Object object = this.chunkyApi();
        ChunkDimension chunkDimension = this.selectedChunkDimension();
        int n = this.selectedChunkSize();
        Location location = this.chunkCenter(chunkDimension);
        if (object == null) {
            this.setChunkStatus("idle.");
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"no signal.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        player.closeInventory();
        this.setChunkStatus("waiting.");
        this.sendWaitingStatus(player);
        BackendOperationToken token = this.nextChunkToken();
        ChunkOperation operation = new ChunkOperation(chunkDimension.bukkitWorldName, player.getUniqueId(), token);
        if (!this.chunkOperations.beginIfAbsent(chunkDimension.bukkitWorldName, token, operation)) {
            this.setChunkStatus("running.");
            return;
        }
        boolean bl = this.startChunkyTask(object, chunkDimension.bukkitWorldName, location.getBlockX(), location.getBlockZ(), n);
        if (bl) {
            this.setChunkStatus("running.");
        } else {
            this.chunkOperations.removeIfCurrent(chunkDimension.bukkitWorldName, token);
            this.setChunkStatus("idle.");
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
        }
    }

    private void cancelChunks(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminChunkActionService.hasChunkSignal(this.chunksAvailableHere(), this.chunkyReady())) {
            this.setChunkStatus("idle.");
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"no signal.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        ChunkDimension chunkDimension = this.selectedChunkDimension();
        if (!this.adminChunkActionService.canCancelChunks(this.chunksRunning())) {
            return;
        }
        Object object = this.chunkyApi();
        boolean bl = this.cancelChunkyTask(object, chunkDimension.bukkitWorldName);
        player.closeInventory();
        if (bl) {
            ChunkOperation operation = this.currentChunkOperation(chunkDimension.bukkitWorldName);
            if (operation != null) this.chunkOperations.removeIfCurrent(chunkDimension.bukkitWorldName, operation.token);
            this.setChunkStatus("idle.");
            player.sendMessage((Component)Component.text((String)"done.", (TextColor)NamedTextColor.GRAY));
        } else {
            player.sendMessage((Component)Component.text((String)"try again.", (TextColor)NamedTextColor.DARK_GRAY));
        }
    }

    private boolean startChunkyTask(Object object, String string, int n, int n2, int n3) {
        try {
            Boolean bl;
            Object object2 = object.getClass().getMethod("startTask", String.class, String.class, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, String.class).invoke(object, string, "circle", n, n2, n3, n3, "concentric");
            return object2 instanceof Boolean && (bl = (Boolean)object2) != false;
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }

    private boolean cancelChunkyTask(Object object, String string) {
        if (object == null) {
            return false;
        }
        try {
            Boolean bl;
            Object object2 = object.getClass().getMethod("cancelTask", String.class).invoke(object, string);
            return object2 instanceof Boolean && (bl = (Boolean)object2) != false;
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }

    private String lastBackupTimestamp(ServerId serverId) {
        this.reloadBackups();
        return this.backupMetadataService.lastCopy(this.backups, serverId.proxyName);
    }

    private ChunkOperation currentChunkOperation(String world) {
        return world == null ? null : this.chunkOperations.current(world)
                .map(BackendOperationRegistry.Entry::operation).orElse(null);
    }

    private boolean chunkParticipant(UUID actor) {
        if (actor == null) return false;
        for (BackendOperationRegistry.Entry<ChunkOperation> entry : this.chunkOperations.snapshot()) {
            if (actor.equals(entry.operation().actor)) return true;
        }
        return false;
    }

    private BackendOperationToken nextChunkToken() {
        long generation = this.chunkGenerationCounter.updateAndGet(value -> value == Long.MAX_VALUE ? 1L : value + 1L);
        return BackendOperationToken.create(generation);
    }

    private void setLastBackupTimestamp(ServerId serverId, String string) {
        if (this.backups == null) {
            this.loadBackups();
        }
        this.backupMetadataService.setLastCopy(this.backups, serverId.proxyName, string);
        this.saveBackups();
    }

    private void startManualBackup(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        ServerId serverId = this.currentServer;
        ManualBackupOperation existing = this.currentBackupOperation(serverId);
        if (existing != null) {
            existing.participants.add(player.getUniqueId());
            player.closeInventory();
            this.sendWaitingStatus(player);
            return;
        }
        String string = BACKUP_TIMESTAMP_FORMAT.format(LocalDateTime.now());
        BackendBackupOperationService.BackupPlan backupPlan = this.backupOperationService.plan(
                this.honeyRoot().toPath(),
                new File(System.getProperty("user.dir")).getAbsoluteFile().toPath(),
                serverId.proxyName,
                string);
        if (!this.backupOperationService.requiredPathsExist(backupPlan)) {
            player.closeInventory();
            player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        BackendOperationToken token = this.nextBackupToken();
        ManualBackupOperation operation = new ManualBackupOperation(player.getUniqueId(), token);
        operation.participants.add(player.getUniqueId());
        if (!this.backupOperations.beginIfAbsent(serverId, token, operation)) {
            ManualBackupOperation current = this.currentBackupOperation(serverId);
            if (current != null) current.participants.add(player.getUniqueId());
            player.closeInventory();
            this.sendWaitingStatus(player);
            return;
        }
        player.closeInventory();
        this.sendWaitingStatus(player);
        try {
            this.prepareLiveBackupSnapshot();
            this.saveIdentities();
            this.savePlaces();
            this.saveSkins();
            this.saveBackups();
            BukkitTask backupTask = Bukkit.getScheduler().runTaskAsynchronously((Plugin)this, () -> {
            boolean bl = false;
            String string2 = "";
            try {
                this.backupOperationService.write(backupPlan, operation.cancellation);
                bl = true;
            }
            catch (IOException | RuntimeException exception) {
                string2 = exception.getMessage();
            }
            boolean bl2 = bl;
            String string3 = string2;
            if (!this.isEnabled()) return;
            try {
                Bukkit.getScheduler().runTask((Plugin)this, () -> this.completeManualBackup(serverId, string, backupPlan, token, bl2, string3));
            }
            catch (RuntimeException exception) {
                this.getLogger().warning("LemonOS could not schedule backup completion for " + serverId.proxyName + ": " + exception.getMessage());
            }
            });
            operation.workerTask.replace(backupTask);
            BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.timeoutManualBackup(serverId, token), BACKUP_TIMEOUT_TICKS);
            operation.timeoutTask.replace(timeoutTask);
        }
        catch (RuntimeException exception) {
            this.abortManualBackup(serverId, token);
            this.getLogger().warning("LemonOS could not start backup for " + serverId.proxyName + ": " + exception.getMessage());
            if (player.isOnline()) player.sendMessage(Component.text("try again.", NamedTextColor.DARK_GRAY));
        }
    }

    private void abortManualBackup(ServerId serverId, BackendOperationToken token) {
        ManualBackupOperation operation = this.backupOperations.removeIfCurrent(serverId, token)
                .map(BackendOperationRegistry.Entry::operation).orElse(null);
        if (operation != null) this.cleanupBackupOperation(operation, true);
    }

    private void completeManualBackup(ServerId serverId, String timestamp,
            BackendBackupOperationService.BackupPlan backupPlan, BackendOperationToken token, boolean succeeded, String error) {
        ManualBackupOperation operation = this.backupOperations.removeIfCurrent(serverId, token)
                .map(BackendOperationRegistry.Entry::operation).orElse(null);
        if (operation == null) return;
        this.cleanupBackupOperation(operation, false);
        Player player = Bukkit.getPlayer(operation.initiator);
        if (succeeded) {
            this.setLastBackupTimestamp(serverId, timestamp);
            this.getLogger().info("LemonOS backup saved for " + serverId.proxyName + ": " + backupPlan.serverBackup.getFileName() + " and " + backupPlan.lemonosDataBackup.getFileName());
            if (player != null && player.isOnline()) player.sendMessage(Component.text("saved.", NamedTextColor.GRAY));
            return;
        }
        this.getLogger().warning("LemonOS backup failed for " + serverId.proxyName + ": " + (error == null || error.isBlank() ? "unknown error" : error));
        if (player != null && player.isOnline()) player.sendMessage(Component.text("try again.", NamedTextColor.DARK_GRAY));
    }

    private void timeoutManualBackup(ServerId serverId, BackendOperationToken token) {
        ManualBackupOperation current = this.currentBackupOperation(serverId);
        if (current == null || !current.token.sameOperation(token) || !current.cancellation.cancel()) return;
        ManualBackupOperation operation = this.backupOperations.removeIfCurrent(serverId, token)
                .map(BackendOperationRegistry.Entry::operation).orElse(null);
        if (operation == null) return;
        Set<UUID> actors = Set.copyOf(operation.participants);
        this.cleanupBackupOperation(operation, false);
        this.getLogger().warning("LemonOS backup timed out for " + serverId.proxyName);
        for (UUID actor : actors) {
            Player player = Bukkit.getPlayer(actor);
            if (player != null && player.isOnline()) {
                player.sendMessage(Component.text("try again.", NamedTextColor.DARK_GRAY));
            }
        }
    }

    private ManualBackupOperation currentBackupOperation(ServerId serverId) {
        return serverId == null ? null : this.backupOperations.current(serverId)
                .map(BackendOperationRegistry.Entry::operation).orElse(null);
    }

    private boolean backupInProgress(ServerId serverId) {
        return this.currentBackupOperation(serverId) != null;
    }

    private boolean backupParticipant(UUID actor) {
        if (actor == null) return false;
        for (BackendOperationRegistry.Entry<ManualBackupOperation> entry : this.backupOperations.snapshot()) {
            if (entry.operation().participants.contains(actor)) return true;
        }
        return false;
    }

    private void removeBackupParticipant(UUID actor) {
        if (actor == null) return;
        for (BackendOperationRegistry.Entry<ManualBackupOperation> entry : this.backupOperations.snapshot()) {
            entry.operation().participants.remove(actor);
        }
    }

    private BackendOperationToken nextBackupToken() {
        long generation = this.backupGenerationCounter.updateAndGet(value -> value == Long.MAX_VALUE ? 1L : value + 1L);
        return BackendOperationToken.create(generation);
    }

    private void cleanupBackupOperation(ManualBackupOperation operation, boolean cancelWork) {
        if (operation == null) return;
        if (cancelWork) operation.cancellation.cancel();
        operation.timeoutTask.cancel();
        operation.workerTask.cancel();
        operation.participants.clear();
    }

    private void prepareLiveBackupSnapshot() {
        Bukkit.savePlayers();
        Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)"save-all flush");
    }

    private List<String> placesLoreText() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (ServerId serverId : ServerId.values()) {
            arrayList.add(this.placeStatusLine(serverId));
        }
        arrayList.add("");
        arrayList.addAll(this.healthSnapshot(false));
        return arrayList;
    }

    private List<String> bedrockPlacesText() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (ServerId serverId : ServerId.values()) {
            arrayList.add(this.placeStatusLine(serverId));
        }
        arrayList.add("");
        arrayList.addAll(this.healthSnapshot(true));
        return arrayList;
    }

    private String peopleHereLore(int n) {
        return n + " here.";
    }

    private String resetRequestCountLore() {
        return this.adminResetNavigationService.countLore();
    }

    private String placeHereLine(String string) {
        return string + " here.";
    }

    private String placeStatusLine(ServerId serverId) {
        String string = this.placeName(serverId);
        if (serverId == this.currentServer) {
            return this.placeHereLine(string);
        }
        String string2 = this.placeDisplayStatus(serverId);
        return string2 == null || string2.isBlank() ? string : string + " " + string2;
    }

    private String placeDisplayStatus(ServerId serverId) {
        if (this.isPlaceWakeable(serverId)) {
            return "";
        }
        return this.isServerAvailable(serverId) ? "open." : "closed.";
    }

    private String placeTargetLore(ServerId serverId) {
        if (serverId == this.currentServer) {
            return "back to spawn.\ncurrent.";
        }
        if (this.isServerAvailable(serverId) || this.isPlaceWakeable(serverId)) {
            return this.placeConfig(serverId, "lore", serverId.lore);
        }
        return this.isServerReady(serverId) ? "closed." : "not ready yet.";
    }

    private List<String> healthSnapshot(boolean bl) {
        Runtime runtime = Runtime.getRuntime();
        long l = runtime.totalMemory() - runtime.freeMemory();
        long l2 = runtime.maxMemory();
        ArrayList<String> arrayList = new ArrayList<String>();
        double d = this.currentCpuPercent();
        arrayList.add(this.tpsLine(this.currentTps()));
        arrayList.add(this.msptLine(this.currentMspt()));
        arrayList.add(this.loadLine(d));
        arrayList.add(this.chunksLine(this.loadedChunks()));
        arrayList.add(this.entitiesLine(this.loadedEntities()));
        arrayList.add(this.memoryLine(l, l2));
        arrayList.add(this.diskLine(this.diskFreeGb()));
        return arrayList;
    }

    private String tpsLine(double d) {
        return this.formatOne(d) + " tps";
    }

    private String msptLine(double d) {
        return this.formatOne(d) + " ms";
    }

    private String loadLine(double d) {
        return d >= 0.0 ? this.formatWhole(d) + "% load" : "load unavailable.";
    }

    private String chunksLine(int n) {
        return this.formatCount(n) + " chunks";
    }

    private String entitiesLine(int n) {
        return this.formatCount(n) + " entities";
    }

    private String memoryLine(long l, long l2) {
        return this.formatGb(l) + " / " + this.formatGb(l2) + " GB";
    }

    private String diskLine(double d) {
        return this.formatWhole(d) + " GB free";
    }

    private double currentTps() {
        double[] dArray = Bukkit.getTPS();
        return dArray.length == 0 ? 0.0 : Math.min(20.0, dArray[0]);
    }

    private double currentMspt() {
        try {
            Object object = Bukkit.class.getMethod("getAverageTickTime", new Class[0]).invoke(null, new Object[0]);
            if (object instanceof Number) {
                Number number = (Number)object;
                return number.doubleValue();
            }
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            // empty catch block
        }
        return 0.0;
    }

    private double currentCpuPercent() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (operatingSystemMXBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean operatingSystemMXBean2 = (com.sun.management.OperatingSystemMXBean)operatingSystemMXBean;
            double d2 = operatingSystemMXBean2.getCpuLoad();
            if (d2 >= 0.0) {
                return d2 * 100.0;
            }
        }
        return -1.0;
    }

    private int loadedChunks() {
        int n = 0;
        for (World world : Bukkit.getWorlds()) {
            n += world.getLoadedChunks().length;
        }
        return n;
    }

    private int loadedEntities() {
        int n = 0;
        for (World world : Bukkit.getWorlds()) {
            n += world.getEntities().size();
        }
        return n;
    }

    private double diskFreeGb() {
        return (double)new File(".").getUsableSpace() / 1024.0 / 1024.0 / 1024.0;
    }

    private String formatOne(double d) {
        return String.format(Locale.US, "%.1f", d);
    }

    private String formatGb(long l) {
        return this.formatOne((double)l / 1024.0 / 1024.0 / 1024.0);
    }

    private String formatWhole(double d) {
        return String.format(Locale.US, "%.0f", d);
    }

    private String formatCount(int n) {
        return String.format(Locale.US, "%,d", n);
    }

    private boolean sameBlockLocation(Location location, Location location2) {
        return location != null && location2 != null && location.getWorld() != null && location2.getWorld() != null && location.getWorld().equals((Object)location2.getWorld()) && location.getBlockX() == location2.getBlockX() && location.getBlockY() == location2.getBlockY() && location.getBlockZ() == location2.getBlockZ();
    }

    private void setTime(String string) {
        World world = Bukkit.getWorld((String)"world");
        if (world != null) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)("minecraft:time set " + string));
        }
    }

    private void setWeather(boolean bl) {
        World world = Bukkit.getWorld((String)"world");
        if (world != null) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)(bl ? "minecraft:weather rain" : "minecraft:weather clear"));
        }
    }

    private void setAdminTime(Player player, String string) {
        if (!this.requireAdmin(player)) {
            return;
        }
        this.setTime(string);
        Bukkit.getScheduler().runTask((Plugin)this, () -> player.sendMessage((Component)Component.text((String)(this.verifyTime(string) ? "done." : "try again."), (TextColor)(this.verifyTime(string) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))));
    }

    private void setAdminWeather(Player player, boolean bl) {
        if (!this.requireAdmin(player)) {
            return;
        }
        this.setWeather(bl);
        Bukkit.getScheduler().runTask((Plugin)this, () -> player.sendMessage((Component)Component.text((String)(this.verifyWeather(bl) ? "done." : "try again."), (TextColor)(this.verifyWeather(bl) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))));
    }

    private void setAdminGamemode(Player player, GameMode gameMode) {
        this.setAdminGamemode(player, player.getUniqueId(), gameMode);
    }

    private void setAdminGamemode(Player player, UUID uUID, GameMode gameMode) {
        if (!this.requireAdmin(player)) {
            return;
        }
        Player player2 = Bukkit.getPlayer((UUID)uUID);
        if (!this.adminPlayerControlService.canOpenGamemode(player2)) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        if (player2.getGameMode() == gameMode) {
            return;
        }
        player2.setGameMode(gameMode);
        Bukkit.getScheduler().runTask((Plugin)this, () -> player.sendMessage((Component)Component.text((String)(this.adminPlayerControlService.gamemodeApplied(player2, gameMode) ? "done." : "try again."), (TextColor)(this.adminPlayerControlService.gamemodeApplied(player2, gameMode) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))));
    }

    private boolean verifyTime(String string) {
        World world = Bukkit.getWorld((String)"world");
        if (world == null) {
            return false;
        }
        long l = world.getTime();
        String string2 = string.toLowerCase(Locale.ROOT);
        if (string2.equals("day")) {
            return l >= 0L && l <= 1500L;
        }
        if (string2.equals("noon")) {
            return l >= 5500L && l <= 6500L;
        }
        if (string2.equals("night")) {
            return l >= 12500L && l <= 13500L;
        }
        if (string2.equals("midnight")) {
            return l >= 17500L && l <= 18500L;
        }
        try {
            long l2 = Long.parseLong(string);
            return Math.abs(l - l2) <= 1L;
        }
        catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    private boolean verifyWeather(boolean bl) {
        World world = Bukkit.getWorld((String)"world");
        return world != null && world.hasStorm() == bl;
    }

    private boolean requireAdmin(Player player) {
        if (this.isAdmin(player)) {
            return true;
        }
        player.closeInventory();
        player.sendMessage((Component)Component.text((String)"out of range.", (TextColor)NamedTextColor.DARK_GRAY));
        return false;
    }

    private void createRequest(Player player, Player player2, RequestKind requestKind) {
        this.meetRequestService.create(player, player2, requestKind, requestKind == RequestKind.VISIT ? "wants to visit you." : "invites you.");
    }

    private void acceptRequest(Player player, BackendMeetRequestService.RequestState<RequestKind> requestState) {
        this.meetRequestService.accept(player, requestState);
    }

    private void declineRequest(BackendMeetRequestService.RequestState<RequestKind> requestState) {
        this.meetRequestService.decline(requestState);
    }

    private void clearRequests(UUID uUID) {
        this.meetRequestService.clearFor(uUID);
    }

    private boolean requestMatchesHolder(CubeeHolder cubeeHolder, BackendMeetRequestService.RequestState<RequestKind> requestState) {
        return cubeeHolder != null && this.meetRequestService.matches(cubeeHolder.requestSender, cubeeHolder.requestKind, requestState);
    }

    private boolean canStartSurvivalChain(Player player) {
        return this.currentServer == ServerId.SURVIVAL && player != null && player.getGameMode() == GameMode.SURVIVAL && !this.isAuthLocked(player);
    }

    private void unlockSurvivalRecipes(Player player) {
        if (this.currentServer != ServerId.SURVIVAL || player == null || !player.isOnline() || this.isAuthLocked(player) || !this.survivalBoolean("survival.recipe-book.unlock-all", true)) {
            return;
        }
        ArrayList<NamespacedKey> arrayList = new ArrayList<NamespacedKey>();
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (!(recipe instanceof Keyed keyed)) continue;
            arrayList.add(keyed.getKey());
        }
        if (!arrayList.isEmpty()) {
            player.discoverRecipes(arrayList);
        }
    }

    private boolean canUseSurvivalRefill(Player player) {
        return this.currentServer == ServerId.SURVIVAL && player != null && player.isOnline() && player.getGameMode() == GameMode.SURVIVAL && !this.isAuthLocked(player) && this.survivalFeatureEnabled("refill");
    }

    private void refillPlacedBlock(Player player, EquipmentSlot equipmentSlot, Material material) {
        if (!this.canUseSurvivalRefill(player) || material == null || !material.isBlock()) {
            return;
        }
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = equipmentSlot == EquipmentSlot.OFF_HAND ? playerInventory.getItemInOffHand() : playerInventory.getItemInMainHand();
        if (itemStack != null && itemStack.getType() == material && itemStack.getAmount() > 0) {
            return;
        }
        if (!this.isEmpty(itemStack)) {
            return;
        }
        int n = this.firstRefillSlot(player, material, true);
        if (n < 0) {
            return;
        }
        ItemStack itemStack2 = playerInventory.getItem(n);
        if (equipmentSlot == EquipmentSlot.OFF_HAND) {
            playerInventory.setItemInOffHand(itemStack2);
        } else {
            playerInventory.setItemInMainHand(itemStack2);
        }
        playerInventory.setItem(n, null);
    }

    private void refillMainHand(Player player, Material material) {
        if (!this.canUseSurvivalRefill(player) || material == null) {
            return;
        }
        PlayerInventory playerInventory = player.getInventory();
        if (!this.isEmpty(playerInventory.getItemInMainHand())) {
            return;
        }
        int n = this.firstRefillSlot(player, material, false);
        if (n < 0) {
            return;
        }
        playerInventory.setItemInMainHand(playerInventory.getItem(n));
        playerInventory.setItem(n, null);
    }

    private boolean shouldRefillBrokenItem(Material material) {
        if (material == null) {
            return false;
        }
        return this.survivalBoolean("survival.refill.tools", true) && this.isRefillTool(material) || this.survivalBoolean("survival.refill.weapons", true) && this.isRefillWeapon(material);
    }

    private boolean isRefillTool(Material material) {
        String string = material.name();
        return string.endsWith("_PICKAXE") || string.endsWith("_AXE") || string.endsWith("_SHOVEL") || string.endsWith("_HOE") || string.equals("SHEARS") || string.equals("FISHING_ROD") || string.equals("FLINT_AND_STEEL") || string.equals("BRUSH");
    }

    private boolean isRefillWeapon(Material material) {
        String string = material.name();
        return string.endsWith("_SWORD") || string.equals("BOW") || string.equals("CROSSBOW") || string.equals("TRIDENT") || string.equals("MACE");
    }

    private int firstRefillSlot(Player player, Material material, boolean bl) {
        PlayerInventory playerInventory = player.getInventory();
        boolean bl2 = this.survivalBoolean("survival.refill.prefer-inventory-first", true);
        if (bl2) {
            int n = this.firstRefillSlotInRange(player, playerInventory, material, bl, 9, playerInventory.getStorageContents().length);
            return n >= 0 ? n : this.firstRefillSlotInRange(player, playerInventory, material, bl, 0, 9);
        }
        return this.firstRefillSlotInRange(player, playerInventory, material, bl, 0, playerInventory.getStorageContents().length);
    }

    private int firstRefillSlotInRange(Player player, PlayerInventory playerInventory, Material material, boolean bl, int n, int n2) {
        int n3 = Math.min(n2, playerInventory.getStorageContents().length);
        int n4 = playerInventory.getHeldItemSlot();
        int n5 = this.cubeeSlot();
        boolean bl2 = this.shouldKeepCubeeItem(player);
        for (int i = Math.max(0, n); i < n3; ++i) {
            ItemStack itemStack;
            if (i == n4 || bl2 && i == n5 || (itemStack = playerInventory.getItem(i)) == null || itemStack.getType() != material || bl && !itemStack.getType().isBlock() || this.isCubee(itemStack) || this.isLoginItem(itemStack)) continue;
            return i;
        }
        return -1;
    }

    private void chopConnectedLogs(Block block, Player player) {
        List<Block> list = this.collectConnectedBlocks(block, block.getType(), this.survivalInt("survival.tree.max-blocks", 160, 1, 512), this::isLog, player, false, ConnectionMode.ALL);
        list = this.orderTreeBlocks(block, list);
        this.startPacedChain(player, list, block.getType(), ChainType.TREE);
    }

    private void mineConnectedVein(Block block, Player player) {
        Material material = block.getType();
        List<Block> list = this.collectConnectedBlocks(block, material, this.survivalInt("survival.miner.max-blocks", 96, 1, 256), material2 -> material2 == material, player, true, ConnectionMode.FACE);
        list = this.orderVeinBlocks(block, list);
        this.startPacedChain(player, list, material, ChainType.VEIN);
    }

    private void harvestConnectedPlants(Block block, Player player) {
        Material material = block.getType();
        List<Block> list = this.collectConnectedBlocks(block, material, this.survivalInt("survival.autocrop.max-blocks", 128, 1, 512), material2 -> material2 == material && this.isSupportedPlantCrop((Material)material2), player, true, ConnectionMode.FLAT).stream().filter(this::isMatureCrop).toList();
        list = this.orderPlantBlocks(block, list);
        this.startPacedChain(player, list, material, ChainType.PLANT);
    }

    private void harvestVerticalAutoPlant(Block block, Player player) {
        String string = this.verticalAutoPlantKey(block.getType());
        if (string == null || !this.survivalBoolean("survival.auto-plant." + string + ".enabled", true)) {
            return;
        }
        if (this.survivalBoolean("survival.auto-plant.require-tool", false) && this.isEmpty(player.getInventory().getItemInMainHand())) {
            return;
        }
        int n = this.verticalAutoPlantBaseDistance(block);
        int n2 = this.survivalInt("survival.auto-plant." + string + ".keep-bottom", 1, 0, 8);
        if (n < n2) {
            return;
        }
        int n3 = this.survivalAutoPlantMaxColumns();
        List<Block> list = this.collectVerticalAutoPlantColumns(block, string, player);
        list = this.orderVerticalAutoPlantBlocks(block, list);
        if (list.size() > n3) {
            list = new ArrayList<Block>(list.subList(0, n3));
        }
        this.startVerticalAutoPlantChain(player, list, block.getType());
    }

    private int survivalAutoPlantMaxColumns() {
        if (this.survival != null && this.survival.isSet("survival.auto-plant.max-columns")) {
            return this.survivalInt("survival.auto-plant.max-columns", 128, 1, 256);
        }
        return this.survivalInt("survival.auto-plant.max-blocks", 128, 1, 256);
    }

    private List<Block> collectVerticalAutoPlantColumns(Block block, String string, Player player) {
        ArrayList<Block> arrayList = new ArrayList<Block>();
        if (block == null || this.isChainReservedByOther(block, player.getUniqueId())) {
            return arrayList;
        }
        Material material = block.getType();
        Block block2 = this.verticalAutoPlantBase(block);
        int n2 = this.survivalInt("survival.auto-plant." + string + ".keep-bottom", 1, 0, 8);
        int n3 = this.survivalPlantRadius(string);
        for (int i = -n3; i <= n3; ++i) {
            for (int j = -n3; j <= n3; ++j) {
                Block block3 = block2.getRelative(i, 0, j);
                if (!this.sameVerticalAutoPlant(material, block3.getType())) continue;
                Block block4 = this.verticalAutoPlantHarvestStart(block3, n2);
                if (block4 == null || this.isChainReservedByOther(block4, player.getUniqueId())) continue;
                arrayList.add(block4);
            }
        }
        return arrayList;
    }

    private Block verticalAutoPlantHarvestStart(Block block, int n) {
        Block block2 = this.verticalAutoPlantBase(block);
        for (int i = 0; i < n; ++i) {
            block2 = block2.getRelative(BlockFace.UP);
        }
        return this.sameVerticalAutoPlant(block.getType(), block2.getType()) ? block2 : null;
    }

    private int survivalPlantRadius(String string) {
        int n = this.survivalInt("survival.auto-plant.radius", 8, 0, 32);
        if (this.survival != null && this.survival.isSet("survival.auto-plant." + string + ".radius")) {
            return this.survivalInt("survival.auto-plant." + string + ".radius", n, 0, 32);
        }
        return n;
    }

    private List<Block> orderVerticalAutoPlantBlocks(Block block, List<Block> list) {
        if (list.size() <= 2) {
            return list;
        }
        ArrayList<Block> arrayList = new ArrayList<Block>();
        boolean bl = this.plantSweepUsesXRows(list);
        List<Integer> list2 = this.plantRows(list, block, bl);
        boolean bl2 = this.plantFirstRowAscending(list, list2, block, bl);
        for (int n : list2) {
            List<Block> list3 = this.verticalAutoPlantRowBlocks(list, n, bl);
            this.sortPlantRow(list3, block, bl, bl2);
            arrayList.addAll(list3);
            bl2 = !bl2;
        }
        return arrayList;
    }

    private List<Block> verticalAutoPlantRowBlocks(List<Block> list, int n, boolean bl) {
        ArrayList<Block> arrayList = new ArrayList<Block>();
        for (Block block : list) {
            if (this.plantRowCoordinate(block, bl) != n) continue;
            arrayList.add(block);
        }
        return arrayList;
    }

    private Block verticalAutoPlantBase(Block block) {
        Material material = block.getType();
        Block block2 = block;
        Block block3 = block2.getRelative(BlockFace.DOWN);
        while (this.sameVerticalAutoPlant(material, block3.getType())) {
            block2 = block3;
            block3 = block2.getRelative(BlockFace.DOWN);
        }
        return block2;
    }

    private int verticalAutoPlantBaseDistance(Block block) {
        int n = 0;
        Material material = block.getType();
        Block block2 = block.getRelative(BlockFace.DOWN);
        while (this.sameVerticalAutoPlant(material, block2.getType())) {
            ++n;
            block2 = block2.getRelative(BlockFace.DOWN);
        }
        return n;
    }

    private boolean sameVerticalAutoPlant(Material material, Material material2) {
        String string = this.verticalAutoPlantKey(material);
        return string != null && string.equals(this.verticalAutoPlantKey(material2));
    }

    private String verticalAutoPlantKey(Material material) {
        if (material == Material.BAMBOO) {
            return "bamboo";
        }
        if (material == Material.SUGAR_CANE) {
            return "sugar-cane";
        }
        if (material == Material.CACTUS) {
            return "cactus";
        }
        if (material == Material.KELP || material == Material.KELP_PLANT) {
            return "kelp";
        }
        return null;
    }

    private void startVerticalAutoPlantChain(Player player, List<Block> list, Material material) {
        if (list.isEmpty()) {
            return;
        }
        this.cancelChain(player.getUniqueId());
        Set<String> set = this.reserveChainBlocks(player.getUniqueId(), list);
        ArrayDeque<Block> arrayDeque = new ArrayDeque<Block>(list);
        int n = arrayDeque.size();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        long l = this.chainPeriod(itemStack);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> {
            if (!player.isOnline()) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            Block block = (Block)arrayDeque.poll();
            if (block == null) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            if (!this.canReachAutoPlantColumn(player, block)) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            if (!this.sameVerticalAutoPlant(material, block.getType())) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            this.breakVerticalAutoPlantBlock(player, block);
            this.advanceChainStatus(player);
        }, l, l);
        ChainState chainState = new ChainState(bukkitTask, set, n, l);
        this.chainTasks.put(player.getUniqueId(), chainState);
        this.showChainStatus(player, chainState);
    }

    private boolean canReachAutoPlantColumn(Player player, Block block) {
        Location location = player.getLocation();
        Location location2 = block.getLocation().add(0.5, 0.5, 0.5);
        int n = this.survivalInt("survival.auto-plant.reach", 10, 1, 32);
        return location.getWorld() != null && location.getWorld().equals((Object)location2.getWorld()) && location.distanceSquared(location2) <= (double)(n * n);
    }

    private void breakVerticalAutoPlantBlock(Player player, Block block) {
        UUID uUID = player.getUniqueId();
        this.chainBreaks.add(uUID);
        try {
            block.breakNaturally(player.getInventory().getItemInMainHand(), true);
        }
        finally {
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.chainBreaks.remove(uUID));
        }
    }

    private List<Block> collectConnectedBlocks(Block block, Material material, int n, Predicate<Material> predicate, Player player, boolean bl, ConnectionMode connectionMode) {
        ArrayList<Block> arrayList = new ArrayList<Block>();
        if (block == null || this.isChainReservedByOther(block, player.getUniqueId())) {
            return arrayList;
        }
        HashSet<Block> hashSet = new HashSet<Block>();
        ArrayDeque<Block> arrayDeque = new ArrayDeque<Block>();
        arrayDeque.add(block);
        hashSet.add(block);
        block0: while (!arrayDeque.isEmpty() && arrayList.size() < n) {
            Block block2 = (Block)arrayDeque.poll();
            arrayList.add(block2);
            for (int[] nArray : this.connectionOffsets(connectionMode)) {
                if (arrayList.size() + arrayDeque.size() >= n) continue block0;
                Block block3 = block2.getRelative(nArray[0], nArray[1], nArray[2]);
                if (hashSet.contains(block3) || this.isChainReservedByOther(block3, player.getUniqueId()) || bl && block3.getType() != material || !predicate.test(block3.getType())) continue;
                hashSet.add(block3);
                arrayDeque.add(block3);
            }
        }
        return arrayList;
    }

    private List<int[]> connectionOffsets(ConnectionMode connectionMode) {
        ArrayList<int[]> arrayList = new ArrayList<int[]>();
        switch (connectionMode.ordinal()) {
            case 0: {
                for (int i = -1; i <= 1; ++i) {
                    for (int j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if (i == 0 && j == 0 && k == 0) continue;
                            arrayList.add(new int[]{i, j, k});
                        }
                    }
                }
                break;
            }
            case 1: {
                arrayList.add(new int[]{1, 0, 0});
                arrayList.add(new int[]{-1, 0, 0});
                arrayList.add(new int[]{0, 1, 0});
                arrayList.add(new int[]{0, -1, 0});
                arrayList.add(new int[]{0, 0, 1});
                arrayList.add(new int[]{0, 0, -1});
                break;
            }
            case 2: {
                arrayList.add(new int[]{1, 0, 0});
                arrayList.add(new int[]{-1, 0, 0});
                arrayList.add(new int[]{0, 0, 1});
                arrayList.add(new int[]{0, 0, -1});
            }
        }
        return arrayList;
    }

    private List<Block> orderTreeBlocks(Block block, List<Block> list) {
        if (list.size() <= 2) {
            return list;
        }
        ArrayList<Block> arrayList = new ArrayList<Block>();
        arrayList.add(block);
        HashSet<Block> hashSet = new HashSet<Block>(list);
        hashSet.remove(block);
        ArrayDeque<Block> arrayDeque = new ArrayDeque<Block>();
        Block block2 = block;
        while (!hashSet.isEmpty()) {
            Block block3;
            List<Block> list2 = this.adjacentBlocks(block2, hashSet);
            if (list2.isEmpty()) {
                while (!arrayDeque.isEmpty()) {
                    block3 = (Block)arrayDeque.peekLast();
                    list2 = this.adjacentBlocks(block3, hashSet);
                    if (!list2.isEmpty()) {
                        block2 = block3;
                        break;
                    }
                    arrayDeque.removeLast();
                }
            }
            if (list2.isEmpty()) {
                list2 = new ArrayList<Block>(hashSet);
            }
            list2.sort(this.treeBranchComparator(block, block2));
            if (list2.size() > 1) {
                arrayDeque.addLast(block2);
            }
            block3 = list2.get(0);
            hashSet.remove(block3);
            arrayList.add(block3);
            block2 = block3;
        }
        return arrayList;
    }

    private Comparator<Block> treeBranchComparator(Block block, Block block2) {
        return (block3, block4) -> {
            int n = Integer.compare(this.treeBranchScore(block2, (Block)block3), this.treeBranchScore(block2, (Block)block4));
            if (n != 0) {
                return n;
            }
            n = Integer.compare(this.blockDistanceSquared(block2, (Block)block3), this.blockDistanceSquared(block2, (Block)block4));
            if (n != 0) {
                return n;
            }
            return this.compareBlocksStable(block, (Block)block3, (Block)block4);
        };
    }

    private int treeBranchScore(Block block, Block block2) {
        int n = block2.getX() - block.getX();
        int n2 = block2.getY() - block.getY();
        int n3 = block2.getZ() - block.getZ();
        int n4 = Math.abs(n) + Math.abs(n3);
        if (n == 0 && n3 == 0 && n2 > 0) {
            return 0;
        }
        if (n2 > 0 && n4 == 1) {
            return 1;
        }
        if (n2 == 0 && n4 == 1) {
            return 2;
        }
        if (n2 == 0) {
            return 3;
        }
        return 4;
    }

    private List<Block> orderVeinBlocks(Block block, List<Block> list) {
        if (list.size() <= 2) {
            return list;
        }
        HashSet<Block> hashSet = new HashSet<Block>(list);
        ArrayList<Block> arrayList = new ArrayList<Block>();
        ArrayDeque<Block> arrayDeque = new ArrayDeque<Block>();
        Block block2 = null;
        Block block3 = block;
        arrayList.add(block);
        hashSet.remove(block);
        while (!hashSet.isEmpty()) {
            FrontierChoice frontierChoice = this.veinFrontierChoice(block, block3, arrayList, arrayDeque, hashSet);
            List<Block> list2 = frontierChoice.blocks;
            list2.sort(this.veinFrontierComparator(block, block2, block3, frontierChoice.anchor, hashSet));
            if (frontierChoice.anchor.equals((Object)block3) && list2.size() > 1) {
                arrayDeque.addLast(block3);
            }
            Block block4 = list2.get(0);
            hashSet.remove(block4);
            arrayList.add(block4);
            block2 = frontierChoice.anchor.equals((Object)block3) ? block3 : null;
            block3 = block4;
        }
        return arrayList;
    }

    private FrontierChoice veinFrontierChoice(Block block, Block block2, List<Block> list, ArrayDeque<Block> arrayDeque, Set<Block> set) {
        List<Block> list2;
        List<Block> list3 = this.adjacentBlocks(block2, set);
        if (!list3.isEmpty()) {
            return new FrontierChoice(block2, list3);
        }
        while (!arrayDeque.isEmpty()) {
            Block block3 = arrayDeque.peekLast();
            list2 = this.adjacentBlocks(block3, set);
            if (!list2.isEmpty()) {
                return new FrontierChoice(block3, list2);
            }
            arrayDeque.removeLast();
        }
        list2 = this.nearPathBlocks(block, list, set);
        if (!list2.isEmpty()) {
            return new FrontierChoice(block, list2);
        }
        return new FrontierChoice(block, new ArrayList<Block>(set));
    }

    private Comparator<Block> veinFrontierComparator(Block block, Block block2, Block block3, Block block4, Set<Block> set) {
        return (block5, block6) -> {
            int n = Integer.compare(this.blockDistanceSquared(block4, (Block)block5), this.blockDistanceSquared(block4, (Block)block6));
            if (n != 0) {
                return n;
            }
            n = Integer.compare(this.veinVerticalScore(block4, (Block)block5), this.veinVerticalScore(block4, (Block)block6));
            if (n != 0) {
                return n;
            }
            n = Integer.compare(this.directionContinuityScore(block2, block3, (Block)block6), this.directionContinuityScore(block2, block3, (Block)block5));
            if (n != 0) {
                return n;
            }
            n = Integer.compare(this.localClusterScore((Block)block6, set), this.localClusterScore((Block)block5, set));
            if (n != 0) {
                return n;
            }
            return this.compareBlocksStable(block, (Block)block5, (Block)block6);
        };
    }

    private int veinVerticalScore(Block block, Block block2) {
        int n = block2.getY() - block.getY();
        if (n == 0) {
            return 0;
        }
        return n > 0 ? 1 : 2;
    }

    private int directionContinuityScore(Block block, Block block2, Block block3) {
        if (block == null) {
            return 0;
        }
        int n = block2.getX() - block.getX();
        int n2 = block2.getY() - block.getY();
        int n3 = block2.getZ() - block.getZ();
        int n4 = block3.getX() - block2.getX();
        int n5 = block3.getY() - block2.getY();
        int n6 = block3.getZ() - block2.getZ();
        return n * n4 + n2 * n5 + n3 * n6;
    }

    private List<Block> nearPathBlocks(Block block, List<Block> list, Set<Block> set) {
        ArrayList<Block> arrayList = new ArrayList<Block>();
        int n = Integer.MAX_VALUE;
        for (Block block2 : set) {
            int n2 = Math.min(this.blockDistanceSquared(block, block2), this.nearestOrderedDistanceSquared(block2, list));
            if (n2 < n) {
                arrayList.clear();
                n = n2;
            }
            if (n2 != n) continue;
            arrayList.add(block2);
        }
        return arrayList;
    }

    private int nearestOrderedDistanceSquared(Block block, List<Block> list) {
        int n = Integer.MAX_VALUE;
        for (Block block2 : list) {
            n = Math.min(n, this.blockDistanceSquared(block2, block));
        }
        return n;
    }

    private int localClusterScore(Block block, Set<Block> set) {
        int n = 0;
        for (Block block2 : set) {
            if (block2.equals((Object)block) || this.blockDistanceSquared(block, block2) > 2) continue;
            ++n;
        }
        return n;
    }

    private List<Block> orderPlantBlocks(Block block, List<Block> list) {
        if (list.size() <= 2) {
            return list;
        }
        ArrayList<Block> arrayList = new ArrayList<Block>();
        arrayList.add(block);
        boolean bl = this.plantSweepUsesXRows(list);
        List<Integer> list2 = this.plantRows(list, block, bl);
        boolean bl2 = this.plantFirstRowAscending(list, list2, block, bl);
        for (int n : list2) {
            List<Block> list3 = this.plantRowBlocks(list, block, n, bl);
            this.sortPlantRow(list3, block, bl, bl2);
            arrayList.addAll(list3);
            bl2 = !bl2;
        }
        return arrayList;
    }

    private boolean plantSweepUsesXRows(List<Block> list) {
        int n = Integer.MAX_VALUE;
        int n2 = Integer.MIN_VALUE;
        int n3 = Integer.MAX_VALUE;
        int n4 = Integer.MIN_VALUE;
        for (Block block : list) {
            n = Math.min(n, block.getX());
            n2 = Math.max(n2, block.getX());
            n3 = Math.min(n3, block.getZ());
            n4 = Math.max(n4, block.getZ());
        }
        return n2 - n >= n4 - n3;
    }

    private List<Integer> plantRows(List<Block> list, Block block, boolean bl) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (Block block2 : list) {
            int n3 = this.plantRowCoordinate(block2, bl);
            if (arrayList.contains(n3)) continue;
            arrayList.add(n3);
        }
        arrayList.sort((n, n2) -> {
            int n3 = this.plantRowCoordinate(block, bl);
            int n4 = Integer.compare(Math.abs(n - n3), Math.abs(n2 - n3));
            if (n4 != 0) {
                return n4;
            }
            return Integer.compare(n, n2);
        });
        return arrayList;
    }

    private List<Block> plantRowBlocks(List<Block> list, Block block, int n, boolean bl) {
        ArrayList<Block> arrayList = new ArrayList<Block>();
        for (Block block2 : list) {
            if (block2.equals((Object)block) || this.plantRowCoordinate(block2, bl) != n) continue;
            arrayList.add(block2);
        }
        return arrayList;
    }

    private boolean plantFirstRowAscending(List<Block> list, List<Integer> list2, Block block2, boolean bl) {
        if (list2.isEmpty()) {
            return true;
        }
        List<Block> list3 = this.plantRowBlocks(list, block2, list2.get(0), bl);
        int n = list3.stream().mapToInt(block -> this.plantLineCoordinate((Block)block, bl)).min().orElse(this.plantLineCoordinate(block2, bl));
        int n2 = list3.stream().mapToInt(block -> this.plantLineCoordinate((Block)block, bl)).max().orElse(this.plantLineCoordinate(block2, bl));
        int n3 = this.plantLineCoordinate(block2, bl);
        return Math.abs(n3 - n) <= Math.abs(n3 - n2);
    }

    private void sortPlantRow(List<Block> list, Block block, boolean bl, boolean bl2) {
        list.sort((block2, block3) -> {
            int n = Integer.compare(this.plantLineCoordinate((Block)block2, bl), this.plantLineCoordinate((Block)block3, bl));
            if (!bl2) {
                n = -n;
            }
            if (n != 0) {
                return n;
            }
            return this.compareBlocksStable(block, (Block)block2, (Block)block3);
        });
    }

    private int plantRowCoordinate(Block block, boolean bl) {
        return bl ? block.getZ() : block.getX();
    }

    private int plantLineCoordinate(Block block, boolean bl) {
        return bl ? block.getX() : block.getZ();
    }

    private List<Block> adjacentBlocks(Block block, Set<Block> set) {
        ArrayList<Block> arrayList = new ArrayList<Block>();
        for (Block block2 : set) {
            if (Math.abs(block2.getX() - block.getX()) > 1 || Math.abs(block2.getY() - block.getY()) > 1 || Math.abs(block2.getZ() - block.getZ()) > 1) continue;
            arrayList.add(block2);
        }
        return arrayList;
    }

    private int blockDistanceSquared(Block block, Block block2) {
        int n = block.getX() - block2.getX();
        int n2 = block.getY() - block2.getY();
        int n3 = block.getZ() - block2.getZ();
        return n * n + n2 * n2 + n3 * n3;
    }

    private int compareBlocksStable(Block block, Block block2, Block block3) {
        int n = Integer.compare(this.blockDistanceSquared(block, block2), this.blockDistanceSquared(block, block3));
        if (n != 0) {
            return n;
        }
        n = Integer.compare(block2.getY(), block3.getY());
        if (n != 0) {
            return n;
        }
        n = Integer.compare(block2.getX(), block3.getX());
        if (n != 0) {
            return n;
        }
        return Integer.compare(block2.getZ(), block3.getZ());
    }

    private void startPacedChain(Player player, List<Block> list, Material material, ChainType chainType) {
        if (list.size() <= 1) {
            return;
        }
        this.cancelChain(player.getUniqueId());
        Set<String> set = this.reserveChainBlocks(player.getUniqueId(), list);
        ArrayDeque<Block> arrayDeque = new ArrayDeque<Block>(list);
        arrayDeque.poll();
        int n2 = arrayDeque.size();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemStack itemStack2 = this.normalizedChainItem(itemStack);
        int n = player.getInventory().getHeldItemSlot();
        long l = this.chainPeriod(itemStack, material, chainType);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> {
            if (!player.isOnline()) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            ItemStack currentItem = player.getInventory().getItemInMainHand();
            if (!this.sameChainHeldItem(player, n, itemStack2)) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            if (!this.isCorrectChainTool(currentItem, material, chainType)) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            Block block = (Block)arrayDeque.peek();
            if (block == null) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            if (!this.canReachChainBlock(player, block) || !this.canBreakChainBlock(itemStack2, block, material, chainType)) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            arrayDeque.poll();
            if (!this.breakExtraBlock(player, block, material, chainType)) {
                this.cancelChain(player.getUniqueId());
                return;
            }
            this.advanceChainStatus(player);
        }, l, l);
        ChainState chainState = new ChainState(bukkitTask, set, n2, l);
        this.chainTasks.put(player.getUniqueId(), chainState);
        this.showChainStatus(player, chainState);
    }

    private boolean sameChainHeldItem(Player player, int n, ItemStack itemStack) {
        if (player.getInventory().getHeldItemSlot() != n) {
            return false;
        }
        ItemStack itemStack2 = player.getInventory().getItemInMainHand();
        if (this.isEmpty(itemStack) || this.isEmpty(itemStack2)) {
            return this.isEmpty(itemStack) && this.isEmpty(itemStack2);
        }
        ItemStack itemStack3 = this.normalizedChainItem(itemStack2);
        return itemStack3 != null && itemStack3.isSimilar(itemStack);
    }

    private ItemStack normalizedChainItem(ItemStack itemStack) {
        if (this.isEmpty(itemStack)) {
            return null;
        }
        ItemStack itemStack2 = itemStack.clone();
        ItemMeta itemMeta = itemStack2.getItemMeta();
        if (itemMeta instanceof Damageable) {
            Damageable damageable = (Damageable)itemMeta;
            damageable.setDamage(0);
            itemStack2.setItemMeta(itemMeta);
        }
        return itemStack2;
    }

    private void cancelChain(UUID uUID) {
        ChainState chainState = this.chainTasks.remove(uUID);
        if (chainState != null) {
            chainState.task.cancel();
            this.releaseChainBlocks(uUID, chainState.blocks);
            this.clearChainStatus(uUID);
        }
    }

    private void completeChain(Player player) {
        if (player == null) {
            return;
        }
        UUID uUID = player.getUniqueId();
        ChainState chainState = this.chainTasks.remove(uUID);
        if (chainState == null) {
            return;
        }
        chainState.task.cancel();
        this.releaseChainBlocks(uUID, chainState.blocks);
        this.clearChainStatus(uUID);
        this.recordHudStat("auto-chain", player, 1);
    }

    private void advanceChainStatus(Player player) {
        if (player == null) {
            return;
        }
        ChainState chainState = this.chainTasks.get(player.getUniqueId());
        if (chainState == null) {
            return;
        }
        chainState.remaining = Math.max(0, chainState.remaining - 1);
        if (chainState.remaining <= 0) {
            this.completeChain(player);
            return;
        }
        chainState.nextStepAtMillis = this.monotonicMillis() + chainState.periodTicks * 50L;
        this.showChainStatus(player, chainState);
    }

    private void showChainStatus(Player player, ChainState chainState) {
        if (player == null || !player.isOnline() || chainState == null || !this.survivalBoolean("survival.chain-status.enabled", true)) {
            return;
        }
        long l = this.monotonicMillis();
        this.publishActionBar(player, BackendActionBarCoordinator.Owner.CHAIN, this.chainStatusComponent(this.chainEtaSeconds(chainState, l)));
    }

    private Component chainStatusComponent(long l) {
        long l2 = Math.max(0L, l);
        return Component.text((String)("working " + l2 + "s"), (TextColor)HoneyPalette.DEFAULT_WHITE);
    }

    private long chainEtaSeconds(ChainState chainState, long l) {
        if (chainState == null || chainState.remaining <= 0) {
            return 0L;
        }
        long l2 = Math.max(0L, chainState.nextStepAtMillis - l);
        long l3 = Math.max(0, chainState.remaining - 1) * Math.max(1L, chainState.periodTicks) * 50L;
        return Math.max(1L, (long)Math.ceil((double)(l2 + l3) / 1000.0));
    }

    private void startChainStatusTask() {
        this.chainStatusTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> this.runActionBarProducer("chain", () -> {
            if (this.currentServer != ServerId.SURVIVAL || this.chainTasks.isEmpty()) {
                return;
            }
            ArrayList<UUID> arrayList = new ArrayList<UUID>();
            for (Map.Entry<UUID, ChainState> entry : List.copyOf(this.chainTasks.entrySet())) {
                UUID uUID = entry.getKey();
                Player player = Bukkit.getPlayer((UUID)uUID);
                if (player == null || !player.isOnline()) {
                    arrayList.add(uUID);
                    continue;
                }
                try {
                    this.showChainStatus(player, entry.getValue());
                }
                catch (RuntimeException exception) {
                    this.logActionBarFailure(player, exception);
                }
            }
            for (UUID uUID : arrayList) {
                this.cancelChain(uUID);
            }
        }), 10L, 10L);
    }

    private void clearChainStatus(UUID uUID) {
        this.delayAtmosphereMusicActionBarResume(uUID);
        Player player = Bukkit.getPlayer((UUID)uUID);
        this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.CHAIN);
    }

    private Set<String> reserveChainBlocks(UUID uUID, List<Block> list) {
        HashSet<String> hashSet = new HashSet<String>();
        for (Block block : list) {
            String string = this.blockKey(block);
            UUID uUID2 = this.chainReservations.get(string);
            if (uUID2 != null && !uUID2.equals(uUID)) continue;
            this.chainReservations.put(string, uUID);
            hashSet.add(string);
        }
        return hashSet;
    }

    private void releaseChainBlocks(UUID uUID, Set<String> set) {
        for (String string : set) {
            if (!uUID.equals(this.chainReservations.get(string))) continue;
            this.chainReservations.remove(string);
        }
    }

    private boolean isChainReservedByOther(Block block, UUID uUID) {
        UUID uUID2 = this.chainReservations.get(this.blockKey(block));
        return uUID2 != null && !uUID2.equals(uUID);
    }

    private String blockKey(Block block) {
        return String.valueOf(block.getWorld().getUID()) + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
    }

    private long chainPeriod(ItemStack itemStack) {
        int n = this.efficiencyLevel(itemStack);
        return Math.max(12L, this.toolBaseChainPeriod(itemStack) - (long)n * 2L);
    }

    private long chainPeriod(ItemStack itemStack, Material material, ChainType chainType) {
        if (chainType != ChainType.TREE && chainType != ChainType.VEIN) {
            return this.chainPeriod(itemStack);
        }
        double d = this.chainHardness(material, chainType);
        double d2 = this.toolMiningSpeed(itemStack);
        if (d <= 0.0 || d2 <= 0.0) {
            return this.chainPeriod(itemStack);
        }
        int n = this.efficiencyLevel(itemStack);
        if (n > 0 && d2 > 1.0) {
            d2 += (double)(n * n + 1);
        }
        long l = (long)Math.ceil(d * 30.0 / d2);
        long l2 = Math.max(6L, (long)Math.ceil((double)l * SURVIVAL_CHAIN_PERIOD_MULTIPLIER) + 2L);
        return Math.max(1L, l2 + this.toolChainTickOffset(itemStack));
    }

    private long toolChainTickOffset(ItemStack itemStack) {
        if (itemStack == null) {
            return 0L;
        }
        String string = itemStack.getType().name();
        if (string.startsWith("WOODEN_")) {
            return -2L;
        }
        if (string.startsWith("STONE_")) {
            return 2L;
        }
        if (string.startsWith("IRON_")) {
            return 4L;
        }
        if (string.startsWith("DIAMOND_") || string.startsWith("NETHERITE_") || string.startsWith("GOLDEN_")) {
            return 3L;
        }
        return 0L;
    }

    private double chainHardness(Material material, ChainType chainType) {
        if (chainType == ChainType.TREE) {
            return 2.0;
        }
        if (material == Material.ANCIENT_DEBRIS) {
            return 30.0;
        }
        String string = material == null ? "" : material.name();
        if (string.startsWith("DEEPSLATE_") && string.endsWith("_ORE")) {
            return 4.5;
        }
        return 1.0;
    }

    private double toolMiningSpeed(ItemStack itemStack) {
        if (itemStack == null) {
            return 0.0;
        }
        String string = itemStack.getType().name();
        if (string.startsWith("WOODEN_")) {
            return 2.0;
        }
        if (string.startsWith("STONE_")) {
            return 4.0;
        }
        if (string.startsWith("IRON_")) {
            return 6.0;
        }
        if (string.startsWith("DIAMOND_")) {
            return 8.0;
        }
        if (string.startsWith("NETHERITE_")) {
            return 9.0;
        }
        if (string.startsWith("GOLDEN_")) {
            return 12.0;
        }
        return 1.0;
    }

    private long toolBaseChainPeriod(ItemStack itemStack) {
        if (itemStack == null) {
            return 24L;
        }
        String string = itemStack.getType().name();
        if (string.startsWith("WOODEN_")) {
            return 29L;
        }
        if (string.startsWith("STONE_")) {
            return 26L;
        }
        if (string.startsWith("GOLDEN_")) {
            return 19L;
        }
        if (string.startsWith("DIAMOND_") || string.startsWith("NETHERITE_")) {
            return 22L;
        }
        return 24L;
    }

    private int efficiencyLevel(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return 0;
        }
        return itemStack.getEnchantments().entrySet().stream().filter(entry -> ((Enchantment)entry.getKey()).getKey().getKey().equalsIgnoreCase("efficiency")).mapToInt(Map.Entry::getValue).max().orElse(0);
    }

    private boolean isCorrectChainTool(ItemStack itemStack, Material material, ChainType chainType) {
        return switch (chainType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.isAxe(itemStack);
            case 1 -> this.isCorrectVeinTool(itemStack, material);
            case 2 -> true;
            case 3 -> true;
        };
    }

    private boolean isChainMaterial(Block block, Material material, ChainType chainType) {
        return switch (chainType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.isLog(block.getType());
            case 1 -> {
                if (block.getType() == material) {
                    yield true;
                }
                yield false;
            }
            case 2 -> block.getType() == material && this.isMatureCrop(block);
            case 3 -> this.sameVerticalAutoPlant(material, block.getType());
        };
    }

    private boolean canBreakChainBlock(ItemStack itemStack, Block block, Material material, ChainType chainType) {
        if (!this.isChainMaterial(block, material, chainType)) {
            return false;
        }
        return chainType != ChainType.VEIN || this.canHarvestVeinBlock(itemStack, block);
    }

    private boolean canReachChainBlock(Player player, Block block) {
        Location location = player.getLocation();
        Location location2 = block.getLocation().add(0.5, 0.5, 0.5);
        return location.getWorld() != null && location.getWorld().equals((Object)location2.getWorld()) && location.distanceSquared(location2) <= 100.0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean breakExtraBlock(Player player, Block block, Material material, ChainType chainType) {
        boolean bl;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (!this.isCorrectChainTool(itemStack, material, chainType) || !this.canBreakChainBlock(itemStack, block, material, chainType)) {
            return false;
        }
        Material material2 = block.getType();
        UUID uUID = player.getUniqueId();
        this.chainBreaks.add(uUID);
        try {
            bl = block.breakNaturally(itemStack, true);
        }
        finally {
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.chainBreaks.remove(uUID));
        }
        if (!bl || block.getType() == material2) {
            return false;
        }
        if (chainType == ChainType.PLANT) {
            this.replantCropIfConfigured(block, player, material);
            return true;
        }
        if (chainType == ChainType.VERTICAL_PLANT) {
            return true;
        }
        return this.damageToolOnce(player, itemStack);
    }

    private boolean damageToolOnce(Player player, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getType().getMaxDurability() <= 0) {
            return true;
        }
        itemStack.damage(1, (LivingEntity)player);
        ItemStack itemStack2 = player.getInventory().getItemInMainHand();
        return itemStack2 != null && itemStack2.getType() != Material.AIR;
    }

    private boolean isAxe(ItemStack itemStack) {
        return itemStack != null && itemStack.getType().name().endsWith("_AXE");
    }

    private boolean isLog(Material material) {
        return Tag.LOGS.isTagged(material) || Tag.CRIMSON_STEMS.isTagged(material) || Tag.WARPED_STEMS.isTagged(material);
    }

    private boolean isVeinMineable(Material material) {
        String string = material.name();
        return string.endsWith("_ORE") || string.equals("ANCIENT_DEBRIS");
    }

    private boolean isCorrectVeinTool(ItemStack itemStack, Material material) {
        return itemStack != null && itemStack.getType().name().endsWith("_PICKAXE");
    }

    private boolean canHarvestVeinBlock(ItemStack itemStack, Block block) {
        return itemStack != null && block != null && this.isCorrectVeinTool(itemStack, block.getType()) && block.isPreferredTool(itemStack);
    }

    private boolean isMatureCrop(Block block) {
        BlockData blockData;
        if (block == null || !this.isSupportedPlantCrop(block.getType()) || !((blockData = block.getBlockData()) instanceof Ageable)) {
            return false;
        }
        Ageable ageable = (Ageable)blockData;
        return ageable.getAge() >= ageable.getMaximumAge();
    }

    private boolean isSupportedPlantCrop(Material material) {
        return material == Material.WHEAT || material == Material.CARROTS || material == Material.POTATOES || material == Material.BEETROOTS || material == Material.NETHER_WART;
    }

    private void scheduleCropReplant(Block block, Player player) {
        if (!this.survivalBoolean("survival.autocrop.replant", false) || block == null || !this.isSupportedPlantCrop(block.getType())) {
            return;
        }
        Material material = block.getType();
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.replantCropIfConfigured(block, player, material));
    }

    private void replantCropIfConfigured(Block block, Player player, Material material) {
        if (!this.survivalBoolean("survival.autocrop.replant", false)
                || block == null
                || !this.isSupportedPlantCrop(material)
                || block.getType() != Material.AIR
                || !this.consumeReplantItem(player, material)) {
            return;
        }
        block.setType(material, false);
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Ageable ageable) {
            ageable.setAge(0);
            block.setBlockData((BlockData)ageable, false);
        }
        this.recordHudStat("grew-here", player, 1);
    }

    private boolean consumeReplantItem(Player player, Material material) {
        Material material2 = this.replantItem(material);
        if (material2 == null) {
            return false;
        }
        PlayerInventory playerInventory = player.getInventory();
        for (int i = 0; i < playerInventory.getStorageContents().length; ++i) {
            ItemStack itemStack = playerInventory.getItem(i);
            if (itemStack == null || itemStack.getType() != material2) continue;
            if (itemStack.getAmount() <= 1) {
                playerInventory.setItem(i, null);
            } else {
                itemStack.setAmount(itemStack.getAmount() - 1);
            }
            return true;
        }
        return false;
    }

    private Material replantItem(Material material) {
        return switch (material) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case NETHER_WART -> Material.NETHER_WART;
            default -> null;
        };
    }

    private boolean handleSurvivalToolSwitch(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        if (!this.survivalFeatureEnabled("tool-switch")
                || !this.canStartSurvivalChain(player)
                || playerInteractEvent.getHand() != EquipmentSlot.HAND
                || playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK
                || !this.survivalSneakAllowed(player, "tool-switch")
                || playerInteractEvent.getClickedBlock() == null) {
            return false;
        }
        Block block = playerInteractEvent.getClickedBlock();
        PlayerInventory playerInventory = player.getInventory();
        int n = playerInventory.getHeldItemSlot();
        ItemStack itemStack = playerInventory.getItem(n);
        if (!this.isAllowedSurvivalSwitchTool(itemStack)) {
            return false;
        }
        if (this.isPreferredSwitchTool(block, itemStack)) {
            return false;
        }
        int n2 = this.bestSurvivalToolSwitchSlot(player, block);
        if (n2 < 0) {
            return false;
        }
        ItemStack itemStack2 = playerInventory.getItem(n2);
        playerInventory.setItem(n, itemStack2);
        playerInventory.setItem(n2, itemStack);
        player.updateInventory();
        playerInteractEvent.setCancelled(true);
        return true;
    }

    private int bestSurvivalToolSwitchSlot(Player player, Block block) {
        PlayerInventory playerInventory = player.getInventory();
        int n = Math.min(playerInventory.getStorageContents().length, 36);
        int n2 = this.survivalInt("survival.tool-switch.min-durability-left", 2, 0, 128);
        int n3 = -1;
        int n4 = Integer.MAX_VALUE;
        for (int i = 9; i < n; ++i) {
            ItemStack itemStack = playerInventory.getItem(i);
            if (!this.isAllowedSurvivalSwitchTool(itemStack) || !this.isPreferredSwitchTool(block, itemStack)) continue;
            int n6 = this.toolDurabilityLeft(itemStack);
            if (n6 >= 0 && n6 < n2) continue;
            int n7 = this.switchToolTier(itemStack.getType());
            if (n7 <= 0) continue;
            if (n7 >= n4) continue;
            n3 = i;
            n4 = n7;
        }
        return n3;
    }

    private boolean isAllowedSurvivalSwitchTool(ItemStack itemStack) {
        if (this.isEmpty(itemStack) || this.isCubee(itemStack) || this.isLoginItem(itemStack)) {
            return false;
        }
        Material material = itemStack.getType();
        if (!this.isSwitchTool(material)) {
            return false;
        }
        return this.survivalBoolean("survival.tool-switch.allow-golden-tools", false) || !material.name().startsWith("GOLDEN_");
    }

    private boolean isPreferredSwitchTool(Block block, ItemStack itemStack) {
        return block != null && this.isAllowedSurvivalSwitchTool(itemStack) && block.isPreferredTool(itemStack) && this.isValidSwitchTool(block, itemStack);
    }

    private boolean isValidSwitchTool(Block block, ItemStack itemStack) {
        try {
            Object object = block.getClass().getMethod("isValidTool", ItemStack.class).invoke(block, itemStack);
            return object instanceof Boolean && (Boolean)object;
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            return block.isPreferredTool(itemStack);
        }
    }

    private boolean isSwitchTool(Material material) {
        if (material == null) {
            return false;
        }
        String string = material.name();
        return string.endsWith("_PICKAXE") || string.endsWith("_AXE") || string.endsWith("_SHOVEL") || string.endsWith("_HOE");
    }

    private int switchToolTier(Material material) {
        if (material == null) {
            return 0;
        }
        String string = material.name();
        if (string.startsWith("WOODEN_") || string.startsWith("GOLDEN_")) {
            return 1;
        }
        if (string.startsWith("STONE_")) {
            return 2;
        }
        if (string.startsWith("IRON_")) {
            return 3;
        }
        if (string.startsWith("DIAMOND_")) {
            return 4;
        }
        if (string.startsWith("NETHERITE_")) {
            return 5;
        }
        return 0;
    }

    private int toolDurabilityLeft(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().getMaxDurability() <= 0) {
            return -1;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof Damageable damageable)) {
            return itemStack.getType().getMaxDurability();
        }
        if (itemMeta.isUnbreakable()) {
            return Integer.MAX_VALUE;
        }
        return itemStack.getType().getMaxDurability() - damageable.getDamage();
    }

    private boolean handleSurvivalChestSort(PlayerInteractEvent playerInteractEvent) {
        if (!this.survivalFeatureEnabled("chest-sort")
                || !this.canStartSurvivalChain(playerInteractEvent.getPlayer())
                || playerInteractEvent.getHand() != EquipmentSlot.HAND
                || playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK
                || !this.survivalSneakAllowed(playerInteractEvent.getPlayer(), "chest-sort")
                || this.survivalBoolean("survival.chest-sort.require-empty-hand", true) && !this.isEmpty(playerInteractEvent.getPlayer().getInventory().getItemInMainHand())
                || playerInteractEvent.getClickedBlock() == null
                || !(playerInteractEvent.getClickedBlock().getState() instanceof Container container)
                || !this.isSurvivalSortableInventory(container.getInventory())) {
            return false;
        }
        this.sortInventory(container.getInventory());
        return true;
    }

    private boolean isSurvivalSortableInventory(Inventory inventory) {
        if (inventory == null) {
            return false;
        }
        List<String> list = this.survival == null ? List.of("CHEST", "BARREL", "SHULKER_BOX") : this.survival.getStringList("survival.chest-sort.containers");
        String string = inventory.getType().name();
        for (String string2 : list) {
            String string3 = String.valueOf(string2).trim().toUpperCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
            if (string.equals(string3)) {
                return true;
            }
        }
        return false;
    }

    private void sortInventory(Inventory inventory) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (ItemStack itemStack : inventory.getStorageContents()) {
            if (itemStack == null || itemStack.getType().isAir()) continue;
            list.add(itemStack.clone());
        }
        if (list.isEmpty()) {
            return;
        }
        Map<String, ItemStack> map = new HashMap<String, ItemStack>();
        for (ItemStack itemStack : list) {
            ItemStack itemStack2 = itemStack.clone();
            int n = itemStack2.getAmount();
            itemStack2.setAmount(1);
            String string = itemStack2.getType().name() + "|" + (itemStack2.hasItemMeta() ? itemStack2.getItemMeta().hashCode() : 0);
            ItemStack itemStack3 = map.computeIfAbsent(string, ignored -> {
                ItemStack itemStack4 = itemStack2.clone();
                itemStack4.setAmount(0);
                return itemStack4;
            });
            itemStack3.setAmount(itemStack3.getAmount() + n);
        }
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        map.values().stream().sorted(Comparator.comparing(itemStack -> itemStack.getType().name())).forEach(itemStack -> {
            int n = itemStack.getAmount();
            while (n > 0) {
                ItemStack itemStack2 = itemStack.clone();
                int n2 = Math.min(itemStack.getMaxStackSize(), n);
                itemStack2.setAmount(n2);
                arrayList.add(itemStack2);
                n -= n2;
            }
        });
        ItemStack[] itemStackArray = new ItemStack[inventory.getStorageContents().length];
        for (int i = 0; i < arrayList.size() && i < itemStackArray.length; ++i) {
            itemStackArray[i] = arrayList.get(i);
        }
        inventory.setStorageContents(itemStackArray);
    }

    private boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir();
    }

    private boolean isBusy(UUID uUID) {
        return this.pendingSkinInputs.contains(uUID) || this.activeSkinChanges.contains(uUID) || this.pendingPrivateNotes.containsKey(uUID) || this.travelStateService.contains(uUID) || this.meetRequestService.hasActive(uUID) || this.sandboxDrawingSessionService.contains(uUID) || this.sandboxPreviewService.hasAny(uUID);
    }

    private boolean cancelActiveDrawing(UUID uUID) {
        BackendSandboxActiveDrawingLifecycleService.DrawingLifecycle drawingLifecycle = this.sandboxActiveDrawingLifecycleService.cancel(this.removeDrawingState(uUID));
        this.applyDrawingLifecycle(uUID, drawingLifecycle);
        return drawingLifecycle.hadDrawing();
    }

    private void finishActiveDrawing(UUID uUID) {
        BackendSandboxActiveDrawingLifecycleService.DrawingLifecycle drawingLifecycle = this.sandboxActiveDrawingLifecycleService.finish(this.removeDrawingState(uUID));
        this.applyDrawingLifecycle(uUID, drawingLifecycle);
    }

    private void clearDrawingPreviews(UUID uUID) {
        for (BackendSandboxPreviewLifecycleService.PreviewKind previewKind : this.sandboxPreviewLifecycleService.clearOrder()) {
            this.cancelPreview(uUID, previewKind);
        }
    }

    private void clearDrawingSession(UUID uUID) {
        BackendSandboxActiveDrawingLifecycleService.DrawingLifecycle drawingLifecycle = this.sandboxActiveDrawingLifecycleService.clearSession(this.removeDrawingState(uUID));
        this.clearDrawingPreviews(uUID);
        this.applyDrawingLifecycle(uUID, drawingLifecycle);
    }

    private void applyDrawingLifecycle(UUID uUID, BackendSandboxActiveDrawingLifecycleService.DrawingLifecycle drawingLifecycle) {
        if (drawingLifecycle.cancelIdleTimeout()) {
            this.cancelSandboxIdleTimeout(uUID);
        }
        if (drawingLifecycle.clearStatus()) {
            this.clearSandboxStatus(uUID);
        }
    }

    private void clearRuntimeIdentityState(UUID uUID) {
        this.cancelAdminSend(uUID, false);
        this.resetSessionIds.remove(uUID);
        this.authLocked.remove(uUID);
        this.authenticatedIdentities.remove(uUID);
        this.pendingIdentityInputs.remove(uUID);
        this.pendingPasscodes.remove(uUID);
        this.passcodeGuidanceShown.remove(uUID);
        this.clearPasscodeFeedback(uUID);
        this.resetInputCounts.remove(uUID);
        this.pendingSkinInputs.remove(uUID);
        this.cancelSkinInputTimeout(uUID);
        this.activeSkinChanges.remove(uUID);
        this.cancelSkinChangeTimeout(uUID);
        this.pendingPrivateNotes.remove(uUID);
        this.cancelSandboxIdleTimeout(uUID);
        this.clearSandboxStatus(uUID);
        this.clearCareWorldStatus(uUID);
        this.bedrockFormTokens.remove(uUID);
        this.bedrockActionTokens.remove(uUID);
        this.proxyAdmins.remove(uUID);
        this.cubeeNavigationService.remove(uUID);
        this.defaultLaunchPending.remove(uUID);
        this.peoplePageIndexes.remove(uUID);
        this.adminPeoplePageIndexes.remove(uUID);
        this.pendingActionBarStatuses.remove(uUID);
        this.removeBackupParticipant(uUID);
        this.actionBarCoordinator.remove(uUID);
        this.actionBarErrorLogNanos.remove(uUID);
    }

    private boolean routeBusyCubeeState(Player player) {
        if (!this.isBusy(player.getUniqueId())) {
            return false;
        }
        this.openRecoveryTick(() -> this.openCubee(player));
        return true;
    }

    private void touchSandboxIdleTimeout(Player player) {
        UUID uUID = player.getUniqueId();
        this.cancelSandboxIdleTimeout(uUID);
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.expireSandboxIdleState(player), 6000L);
        this.sandboxDrawingSessionService.setIdleTimeout(uUID, bukkitTask);
    }

    private void cancelSandboxIdleTimeout(UUID uUID) {
        BukkitTask bukkitTask = this.sandboxDrawingSessionService.removeIdleTimeout(uUID);
        if (bukkitTask != null) {
            bukkitTask.cancel();
        }
    }

    private void expireSandboxIdleState(Player player) {
        UUID uUID = player.getUniqueId();
        this.sandboxDrawingSessionService.removeIdleTimeout(uUID);
        boolean bl = this.removeDrawingState(uUID);
        boolean bl2 = this.sandboxPreviewService.clearAllFor(uUID);
        BackendSandboxActiveDrawingLifecycleService.IdleExpiry idleExpiry = this.sandboxActiveDrawingLifecycleService.expireIdle(bl, bl2, player.isOnline());
        if (idleExpiry.clearStatus()) {
            this.clearSandboxStatus(uUID);
        }
        if (idleExpiry.sendNothingChanged()) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
        }
    }

    private void cancelClonePreview(UUID uUID) {
        this.cancelPreview(uUID, BackendSandboxPreviewLifecycleService.PreviewKind.CLONE);
    }

    private void cancelClearPreview(UUID uUID) {
        this.cancelPreview(uUID, BackendSandboxPreviewLifecycleService.PreviewKind.CLEAR);
    }

    private void cancelRotatePreview(UUID uUID) {
        this.cancelPreview(uUID, BackendSandboxPreviewLifecycleService.PreviewKind.ROTATE);
    }

    private void cancelFlipPreview(UUID uUID) {
        this.cancelPreview(uUID, BackendSandboxPreviewLifecycleService.PreviewKind.FLIP);
    }

    private void cancelPreview(UUID uUID, BackendSandboxPreviewLifecycleService.PreviewKind previewKind) {
        this.removePreview(uUID, previewKind);
        this.cancelSandboxIdleTimeout(uUID);
        if (!this.hasSandboxStatusState(uUID)) {
            this.clearSandboxStatus(uUID);
        }
    }

    private boolean hasPreview(UUID uUID, BackendSandboxPreviewLifecycleService.PreviewKind previewKind) {
        return switch (previewKind) {
            case CLONE -> this.sandboxPreviewService.hasClone(uUID);
            case CLEAR -> this.sandboxPreviewService.hasClear(uUID);
            case ROTATE -> this.sandboxPreviewService.hasRotate(uUID);
            case FLIP -> this.sandboxPreviewService.hasFlip(uUID);
        };
    }

    private Object removePreview(UUID uUID, BackendSandboxPreviewLifecycleService.PreviewKind previewKind) {
        return switch (previewKind) {
            case CLONE -> this.sandboxPreviewService.removeClone(uUID);
            case CLEAR -> this.sandboxPreviewService.removeClear(uUID);
            case ROTATE -> this.sandboxPreviewService.removeRotate(uUID);
            case FLIP -> this.sandboxPreviewService.removeFlip(uUID);
        };
    }

    private void openPreviewConfirm(Player player, BackendSandboxPreviewLifecycleService.PreviewKind previewKind) {
        switch (previewKind) {
            case CLONE -> this.openCloneConfirm(player);
            case CLEAR -> this.openClearConfirm(player);
            case ROTATE -> this.openRotateConfirm(player);
            case FLIP -> this.openFlipConfirm(player);
        }
    }

    private void openBedrockHome(Player player) {
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title(this.homeTitle())).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        if (this.currentServer != ServerId.LOBBY && this.peopleShortcutPublic(player)) {
            this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.PEOPLE, this.homePeopleStatus(player));
        }
        this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.PLACES, null);
        if (this.currentServer == ServerId.SURVIVAL) {
            Location location = this.survivalHomeLocation(player);
            this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.HOME, location == null ? "not ready yet." : Ui.Home.HOME.lore());
        }
        if (this.sandboxAvailable(player)) {
            this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.SANDBOX, null);
        }
        if (this.isAdmin(player)) {
            this.addBedrockHomeButton(builder, arrayList, player, Ui.Home.CARE, null);
        }
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockHomeButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String lore) {
        boolean homeButton = buttonSpec == Ui.Home.HOME;
        boolean sandboxButton = buttonSpec == Ui.Home.SANDBOX;
        BackendCubeeRoutingService.HomeAction action = this.cubeeRoutingService.homeAction(
                buttonSpec.slot(),
                Ui.Home.LOOK.slot(),
                Ui.Home.CARE.slot(),
                false,
                this.isAdmin(player),
                this.currentServer == ServerId.LOBBY,
                homeButton || this.currentServer == ServerId.SURVIVAL && !sandboxButton,
                this.currentServer != ServerId.LOBBY && this.peopleShortcutPublic(player),
                sandboxButton || this.sandboxAvailable(player) && !homeButton);
        switch (action) {
            case PEOPLE -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> this.openLastPeople(player));
            case PLACES -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openGo(player));
            case SURVIVAL_HOME -> this.bedrockButton(builder, actions, buttonSpec, lore, () -> {
                if (this.survivalHomeLocation(player) != null) {
                    this.returnSurvivalHome(player);
                } else {
                    this.openBedrockHome(player);
                }
            });
            case SANDBOX -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openDrawing(player));
            case CARE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdmin(player));
            case LOOK, NONE -> {
            }
        }
    }

    private void openBedrockAdmin(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        this.switchCubeeRoot(player, CubeeRoot.CARE);
        this.clearCareWorldStatus(player.getUniqueId());
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title(this.careTitle())).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminRootButton(builder, arrayList, player, Ui.Care.PEOPLE, this.peopleHereLore(Bukkit.getOnlinePlayers().size()));
        this.addBedrockAdminRootButton(builder, arrayList, player, Ui.Care.REQUESTS, this.resetRequestCountLore());
        this.addBedrockAdminRootButton(builder, arrayList, player, Ui.Care.KEYS, null);
        this.addBedrockAdminRootButton(builder, arrayList, player, Ui.Care.WORLD, null);
        this.addBedrockAdminRootButton(builder, arrayList, player, Ui.Care.HOME, null);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminRootButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String lore) {
        BackendAdminRootClickService.AdminRootAction action = this.adminRootClickService.action(
                buttonSpec.slot(),
                Ui.Care.HOME.slot(),
                Ui.Care.PEOPLE.slot(),
                Ui.Care.REQUESTS.slot(),
                Ui.Care.KEYS.slot(),
                Ui.Care.WORLD.slot(),
                Ui.Care.ATMOSPHERE.slot(),
                Ui.Care.UPKEEP.slot(),
                false);
        switch (action) {
            case HOME -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                this.switchCubeeRoot(player, CubeeRoot.CUBEE);
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openCubee(player);
            });
            case PEOPLE -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> this.openLastAdminPeople(player));
            case REQUESTS -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> this.openAdminRequests(player, 0));
            case KEYS -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminKeys(player));
            case TOGGLE_WORLD -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openBedrockAdminWorld(player));
            case ATMOSPHERE, UPKEEP -> {
            }
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminWorld(Player player) {
        if (!this.requireAdmin(player)) return;
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("World"))
                .content(String.join((CharSequence)"\n", this.bedrockPlacesText()));
        ArrayList<Runnable> actions = new ArrayList<Runnable>();
        this.bedrockButton(builder, actions, Ui.Care.ATMOSPHERE, () -> this.openBedrockAdminAtmosphere(player));
        this.bedrockButton(builder, actions, Ui.Shared.FORM_BACK, () -> this.openBedrockAdmin(player));
        this.sendBedrockForm(player, builder, actions);
    }

    private void openBedrockAdminKeys(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Keys")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminKeysButton(builder, arrayList, player, Ui.Care.KEY_PEOPLE, null);
        this.addBedrockAdminKeysButton(builder, arrayList, player, Ui.Care.KEY_HOLDERS, this.keyHoldersStatus());
        this.addBedrockAdminKeysButton(builder, arrayList, player, Ui.Shared.FORM_BACK, null);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminKeysButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String lore) {
        BackendAdminKeysClickService.AdminKeysAction action = this.adminKeysClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                Ui.Care.KEY_PEOPLE.slot(),
                Ui.Care.KEY_HOLDERS.slot());
        switch (action) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openAdmin(player);
            });
            case GIVE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminKeyPeople(player, 0));
            case HOLDERS -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> this.openAdminKeyHolders(player, 0));
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminKeyHolders(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Holders")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        int virtualSlot = 1000;
        for (String string : this.adminAccessNavigationService.holderNames(player)) {
            this.addBedrockAdminKeyHolderListButton(builder, arrayList, player, new Ui.ButtonSpec(virtualSlot++, Material.PLAYER_HEAD, string, null), string);
        }
        this.addBedrockAdminKeyHolderListButton(builder, arrayList, player, Ui.Shared.FORM_BACK, null);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminKeyHolderListButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String holderName) {
        Map<Integer, String> slotKeys = new HashMap<>();
        if (holderName != null) {
            slotKeys.put(buttonSpec.slot(), holderName);
        }
        BackendAdminPagedSelectionClickService.SelectionResult clickResult = this.adminPagedSelectionClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                Ui.Care.KEY_FIND.slot(),
                slotKeys);
        switch (clickResult.action()) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminKeys(player));
            case SELECT -> this.bedrockButton((Object)builder, actions, buttonSpec, null, () -> this.openAdminTakeKeyConfirm(player, clickResult.selectedName(), 0));
            case NEXT_PAGE, NONE -> {
            }
        }
    }

    private void openBedrockAdminTakeKeyConfirm(Player player, String string) {
        if (!this.requireAdmin(player)) {
            return;
        }
        boolean bl = this.adminAccessActionService.isSelfHolder(player, string);
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title(bl ? "Leave the keys?" : "Take their key?")).content(string);
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        Ui.ButtonSpec confirmButton = bl ? Ui.Care.STEP_AWAY_CONFIRM : Ui.Care.TAKE_KEY_CONFIRM;
        this.addBedrockAdminKeyTakeConfirmButton(builder, arrayList, player, string, confirmButton, confirmButton.slot());
        this.addBedrockAdminKeyTakeConfirmButton(builder, arrayList, player, string, Ui.CareSelf.CANCEL, confirmButton.slot());
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminKeyTakeConfirmButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, String accessName, Ui.ButtonSpec buttonSpec, int confirmSlot) {
        BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(
                buttonSpec.slot(),
                confirmSlot,
                Ui.CareSelf.CANCEL.slot(),
                accessName != null);
        switch (action) {
            case CONFIRM -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                if (this.setAccessAdmin(player, accessName, false)) {
                    this.sendWaitingStatus(player);
                    FloodgateApi.getInstance().closeForm(player.getUniqueId());
                }
            });
            case CANCEL -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
                FloodgateApi.getInstance().closeForm(player.getUniqueId());
            });
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminKeyPeople(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Give")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        int virtualSlot = 1000;
        for (String string : this.adminAccessNavigationService.candidateNames()) {
            this.addBedrockAdminKeyPeopleListButton(builder, arrayList, player, new Ui.ButtonSpec(virtualSlot++, Material.PLAYER_HEAD, string, null), string);
        }
        this.addBedrockAdminKeyPeopleListButton(builder, arrayList, player, Ui.Shared.FORM_BACK, null);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminKeyPeopleListButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String accessName) {
        Map<Integer, String> slotKeys = new HashMap<>();
        if (accessName != null) {
            slotKeys.put(buttonSpec.slot(), accessName);
        }
        BackendAdminPagedSelectionClickService.SelectionResult clickResult = this.adminPagedSelectionClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                Ui.Care.FIND.slot(),
                slotKeys);
        switch (clickResult.action()) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminKeys(player));
            case SELECT -> this.bedrockButton((Object)builder, actions, buttonSpec, null, () -> this.openAdminKeyGive(player, clickResult.selectedName(), 0));
            case NEXT_PAGE, NONE -> {
            }
        }
    }

    private void openBedrockAdminKeyGive(Player player, String string) {
        if (!this.requireAdmin(player)) {
            return;
        }
        String string2 = this.adminAccessActionService.normalize(string);
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Let them care?")).content(string2);
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminKeyGiveButton(builder, arrayList, player, string2, 0, Ui.Care.CARE_KEY);
        this.addBedrockAdminKeyGiveButton(builder, arrayList, player, string2, 0, Ui.CareSelf.CANCEL);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminKeyGiveButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, String accessName, int pageIndex, Ui.ButtonSpec buttonSpec) {
        BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(
                buttonSpec.slot(),
                Ui.Care.CARE_KEY.slot(),
                Ui.CareSelf.CANCEL.slot(),
                accessName != null);
        switch (action) {
            case CONFIRM -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                if (!this.adminAccessActionService.canGive(accessName)) {
                    player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
                    this.openAdminKeyPeople(player, pageIndex);
                    return;
                }
                if (this.setAccessAdmin(player, accessName, true)) {
                    this.sendWaitingStatus(player);
                    FloodgateApi.getInstance().closeForm(player.getUniqueId());
                }
            });
            case CANCEL -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
                FloodgateApi.getInstance().closeForm(player.getUniqueId());
            });
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminPeople(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        this.rememberAdminPeoplePageIndex(player, this.currentAdminPeoplePageIndex(player));
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("People")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        int virtualSlot = 1000;
        for (Player player2 : this.adminPeopleNavigationService.listPeople(player)) {
            this.addBedrockAdminPeopleListButton(builder, arrayList, player, new Ui.ButtonSpec(virtualSlot++, Material.PLAYER_HEAD, player2.getName(), null), player2.getUniqueId(), this.adminPeopleNavigationService.bedrockStatus(player, player2));
        }
        this.addBedrockAdminPeopleListButton(builder, arrayList, player, Ui.Shared.FORM_BACK, null, null);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminPeopleListButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, UUID targetId, String lore) {
        Map<Integer, UUID> slotTargets = new HashMap<>();
        if (targetId != null) {
            slotTargets.put(buttonSpec.slot(), targetId);
        }
        BackendAdminPeopleClickService.AdminPeopleClick adminPeopleClick = this.adminPeopleClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                Ui.Care.FIND.slot(),
                slotTargets);
        switch (adminPeopleClick.action()) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openAdmin(player);
            });
            case SELECT -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> {
                if (adminPeopleClick.target().equals(player.getUniqueId())) {
                    this.openCareSelf(player, 0);
                    return;
                }
                this.openAdminPlayerIfCurrent(player, adminPeopleClick.target(), 0);
            });
            case NEXT_PAGE, NONE -> {
            }
        }
    }

    private void openBedrockAdminPlayer(Player player, Player player2) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title(player2.getName())).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminPlayerActionButton(builder, arrayList, player, player2, 4, player2.getName(), "look after them.");
        this.addBedrockAdminPlayerActionButton(builder, arrayList, player, player2, Ui.CarePlayer.VISIT);
        this.addBedrockAdminPlayerActionButton(builder, arrayList, player, player2, Ui.CarePlayer.INVITE);
        this.addBedrockAdminPlayerActionButton(builder, arrayList, player, player2, Ui.CarePlayer.MESSAGE);
        this.addBedrockAdminPlayerActionButton(builder, arrayList, player, player2, Ui.Shared.FORM_BACK);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminPlayerActionButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, Ui.ButtonSpec buttonSpec) {
        this.addBedrockAdminPlayerActionButton(builder, actions, player, target, buttonSpec.slot(), buttonSpec.title(), buttonSpec.lore());
    }

    private void addBedrockAdminPlayerActionButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, int slot, String title, String lore) {
        BackendAdminPlayerClickService.AdminPlayerAction action = this.adminPlayerClickService.action(
                slot,
                Ui.Shared.FORM_BACK.slot(),
                4,
                12,
                13,
                14);
        switch (action) {
            case BACK -> this.bedrockButton((Object)builder, actions, title, lore, () -> this.openLastAdminPeople(player));
            case CONTROL -> this.bedrockButton((Object)builder, actions, title, lore, () -> this.openAdminPlayerControlIfCurrent(player, target.getUniqueId(), 0));
            case VISIT -> this.bedrockButton((Object)builder, actions, title, lore, () -> this.adminVisitIfCurrent(player, target.getUniqueId()));
            case INVITE -> this.bedrockButton((Object)builder, actions, title, lore, () -> this.adminInviteIfCurrent(player, target.getUniqueId()));
            case MESSAGE -> {
                if (!this.adminPeopleActionService.canStartMessage(target)) {
                    this.bedrockButton((Object)builder, actions, title, "busy.", () -> {});
                } else {
                    this.bedrockButton((Object)builder, actions, title, lore, () -> this.startPrivateNoteIfCurrent(player, target.getUniqueId()));
                }
            }
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminPlayerControl(Player player, Player player2, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title(player2.getName())).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminPlayerControlActionButton(builder, arrayList, player, player2, n, new Ui.ButtonSpec(Ui.CareSelf.GAMEMODE.slot(), this.gamemodeMaterial(player2.getGameMode()), Ui.CareSelf.GAMEMODE.title(), "choose their mode."));
        this.addBedrockAdminPlayerControlActionButton(builder, arrayList, player, player2, n, Ui.CareSelf.TARGET_CLEAR);
        this.addBedrockAdminPlayerControlActionButton(builder, arrayList, player, player2, n, Ui.CarePlayer.SEND);
        this.addBedrockAdminPlayerControlActionButton(builder, arrayList, player, player2, n, Ui.Shared.FORM_BACK);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminPlayerControlActionButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, int pageIndex, Ui.ButtonSpec buttonSpec) {
        BackendAdminPlayerControlClickService.AdminPlayerControlAction action = this.adminPlayerControlClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                12,
                13,
                14);
        switch (action) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminPlayerIfCurrent(player, target.getUniqueId(), pageIndex));
            case GAMEMODE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminGamemodeIfCurrent(player, target.getUniqueId(), this.adminControlPageMarker(pageIndex)));
            case CLEAR -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                Player currentTarget = Bukkit.getPlayer((UUID)target.getUniqueId());
                if (this.adminPeopleActionService.canTarget(player, currentTarget)) {
                    this.openBedrockAdminPlayerClearConfirm(player, currentTarget, pageIndex);
                } else {
                    this.openAdminPeople(player, pageIndex);
                }
            });
            case SEND -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminPlayerSendPlaces(player, target, pageIndex));
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminPlayerSendPlaces(Player player, Player target, int pageIndex) {
        if (!this.requireAdmin(player) || !this.adminPeopleActionService.canTarget(player, target)) {
            this.openAdminPeople(player, pageIndex);
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Send / Places")).content("");
        ArrayList<Runnable> actions = new ArrayList<Runnable>();
        ServerId targetServer = this.adminTargetServer(target);
        if (targetServer == null) {
            this.openAdminPeople(player, pageIndex);
            return;
        }
        for (ServerId destination : this.goTargets()) {
            BackendPlacesClickService.PlacesClick<ServerId> click = this.placesClickService.action(
                    this.placeLogicalSlot(destination), Ui.Shared.FORM_BACK.slot(), targetServer,
                    ServerId.LOBBY, ServerId.SURVIVAL, ServerId.CREATIVE);
            Runnable action = click.action() == BackendPlacesClickService.PlacesClickAction.RETURN_SPAWN
                    ? () -> this.sendAdminTargetToCurrentSpawn(player, target)
                    : () -> this.startAdminSend(player, target, destination);
            if (click.action() == BackendPlacesClickService.PlacesClickAction.RETURN_SPAWN) {
                this.bedrockButton(builder, actions, this.placeName(destination), "current.", action);
            } else {
                this.bedrockButton(builder, actions, this.placeTargetSpec(0, destination), action);
            }
        }
        this.bedrockButton(builder, actions, Ui.Shared.FORM_BACK, () -> this.openAdminPlayerControlIfCurrent(player, target.getUniqueId(), pageIndex));
        this.sendBedrockForm(player, builder, actions);
    }

    private ServerId adminTargetServer(Player target) {
        return target != null && target.isOnline() ? this.currentServer : null;
    }

    private void openBedrockAdminPlayerClearConfirm(Player player, Player player2, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Clear their things?")).content(player2.getName());
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminPlayerClearConfirmButton(builder, arrayList, player, player2, n, Ui.CareSelf.TARGET_CLEAR_CONFIRM);
        this.addBedrockAdminPlayerClearConfirmButton(builder, arrayList, player, player2, n, Ui.CareSelf.CANCEL);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminPlayerClearConfirmButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, int pageIndex, Ui.ButtonSpec buttonSpec) {
        BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(
                buttonSpec.slot(),
                Ui.CareSelf.TARGET_CLEAR_CONFIRM.slot(),
                Ui.CareSelf.CANCEL.slot());
        switch (action) {
            case CONFIRM -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                Player currentTarget = Bukkit.getPlayer((UUID)target.getUniqueId());
                if (!this.adminPeopleActionService.canTarget(player, currentTarget)) {
                    this.openAdminPeople(player, pageIndex);
                    return;
                }
                this.clearAdminTargetInventory(player, currentTarget);
            });
            case CANCEL -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
                this.openAdminPlayerControlIfCurrent(player, target.getUniqueId(), pageIndex);
            });
            case NONE -> {
            }
        }
    }

    private void openBedrockCareSelf(Player player, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title(player.getName())).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockCareSelfButton(builder, arrayList, player, n, new Ui.ButtonSpec(Ui.CareSelf.GAMEMODE.slot(), this.gamemodeMaterial(player.getGameMode()), Ui.CareSelf.GAMEMODE.title(), Ui.CareSelf.GAMEMODE.lore()));
        this.addBedrockCareSelfButton(builder, arrayList, player, n, Ui.CareSelf.CLEAR);
        this.addBedrockCareSelfButton(builder, arrayList, player, n, Ui.Shared.FORM_BACK);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockCareSelfButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, int pageIndex, Ui.ButtonSpec buttonSpec) {
        BackendAdminSelfClickService.AdminSelfAction action = this.adminSelfClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                Ui.CareSelf.GAMEMODE.slot(),
                Ui.CareSelf.CLEAR.slot());
        switch (action) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminPeople(player, pageIndex));
            case GAMEMODE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdminGamemode(player, player, pageIndex));
            case CLEAR -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openBedrockCareSelfClearConfirm(player, pageIndex));
            case NONE -> {
            }
        }
    }

    private void openBedrockCareSelfClearConfirm(Player player, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Clear your things?")).content(player.getName());
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockCareSelfClearConfirmButton(builder, arrayList, player, n, Ui.CareSelf.CLEAR_CONFIRM);
        this.addBedrockCareSelfClearConfirmButton(builder, arrayList, player, n, Ui.CareSelf.CANCEL);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockCareSelfClearConfirmButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, int pageIndex, Ui.ButtonSpec buttonSpec) {
        BackendAdminConfirmClickService.ConfirmAction action = this.adminConfirmClickService.action(
                buttonSpec.slot(),
                Ui.CareSelf.CLEAR_CONFIRM.slot(),
                Ui.CareSelf.CANCEL.slot());
        switch (action) {
            case CONFIRM -> this.bedrockButton(builder, actions, buttonSpec, () -> this.clearCareSelfInventory(player));
            case CANCEL -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
                this.openCareSelf(player, pageIndex);
            });
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminRequests(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Requests")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        int virtualSlot = 1000;
        for (String string : this.adminResetNavigationService.tokens()) {
            String string2 = this.adminResetNavigationService.displayName(string);
            this.addBedrockAdminRequestListButton(builder, arrayList, player, new Ui.ButtonSpec(virtualSlot++, Material.PLAYER_HEAD, string2, null), string, this.adminResetNavigationService.requestLore(string2));
        }
        this.addBedrockAdminRequestListButton(builder, arrayList, player, Ui.Shared.FORM_BACK, null, null);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminRequestListButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, String token, String lore) {
        Map<Integer, String> slotKeys = new HashMap<>();
        if (token != null) {
            slotKeys.put(buttonSpec.slot(), token);
        }
        BackendAdminRequestsClickService.AdminRequestsClickResult clickResult = this.adminRequestsClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                Ui.Care.REVIEW.slot(),
                slotKeys);
        switch (clickResult.action) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openAdmin(player));
            case SELECT -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> this.openAdminReset(player, clickResult.selectedToken, 0));
            case NEXT_PAGE, NONE -> {
            }
        }
    }

    private void openBedrockAdminReset(Player player, String string) {
        if (!this.requireAdmin(player)) {
            return;
        }
        String string2 = this.adminResetNavigationService.displayName(string);
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Let them reset?")).content(this.adminResetNavigationService.requestLore(string2));
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminResetButton(builder, arrayList, player, string, Ui.Reset.ALLOW);
        this.addBedrockAdminResetButton(builder, arrayList, player, string, Ui.Reset.NOT_NOW);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminResetButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, String token, Ui.ButtonSpec buttonSpec) {
        BackendAdminResetClickService.AdminResetAction action = this.adminResetClickService.action(
                buttonSpec.slot(),
                Ui.Reset.ALLOW.slot(),
                Ui.Reset.NOT_NOW.slot());
        switch (action) {
            case ALLOW -> this.bedrockButton(builder, actions, buttonSpec, () -> this.allowResetRequest(player, token));
            case DENY -> this.bedrockButton(builder, actions, buttonSpec, () -> this.denyResetRequest(player, token));
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminAtmosphere(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Atmosphere")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Atmosphere.DAY);
        this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Atmosphere.NIGHT);
        this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Atmosphere.RAIN);
        this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Atmosphere.CLEAR);
        this.addBedrockAdminAtmosphereButton(builder, arrayList, player, Ui.Shared.FORM_BACK);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminAtmosphereButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec) {
        BackendAdminAtmosphereClickService.AdminAtmosphereClick click = this.adminAtmosphereClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                this.adminWorldNavigationService);
        switch (click.action()) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openBedrockAdminWorld(player));
            case TIME -> this.bedrockButton((Object)builder, actions, buttonSpec, null, () -> this.setAdminTime(player, click.time()));
            case WEATHER -> this.bedrockButton((Object)builder, actions, buttonSpec, null, () -> this.setAdminWeather(player, click.weather()));
            case NONE -> {
            }
        }
    }

    private void openBedrockAdminGamemode(Player player) {
        this.openBedrockAdminGamemode(player, player, 0);
    }

    private void openBedrockAdminGamemode(Player player, Player player2, int n) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminPlayerControlService.canOpenGamemode(player2)) {
            this.openAdminPeople(player, n);
            return;
        }
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Mode")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockAdminGamemodeButton(builder, arrayList, player, player2, n, Ui.Gamemode.ADVENTURE);
        this.addBedrockAdminGamemodeButton(builder, arrayList, player, player2, n, Ui.Gamemode.SURVIVAL);
        this.addBedrockAdminGamemodeButton(builder, arrayList, player, player2, n, Ui.Gamemode.CREATIVE);
        this.addBedrockAdminGamemodeButton(builder, arrayList, player, player2, n, Ui.Gamemode.SPECTATOR);
        this.addBedrockAdminGamemodeButton(builder, arrayList, player, player2, n, Ui.Shared.FORM_BACK);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockAdminGamemodeButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, int pageIndex, Ui.ButtonSpec buttonSpec) {
        BackendAdminGamemodeClickService.AdminGamemodeClick click = this.adminGamemodeClickService.action(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                target.getGameMode());
        switch (click.action()) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                if (this.adminPlayerControlService.isSelf(player, target)) {
                    this.openAdminPeople(player, pageIndex);
                } else {
                    this.openAdminPlayerIfCurrent(player, target.getUniqueId(), pageIndex);
                }
            });
            case SELECT -> {
                GameMode gameMode = click.gameMode();
                this.bedrockButton((Object)builder, actions, buttonSpec, target.getGameMode() == gameMode ? "current." : null, () -> this.setAdminGamemode(player, target.getUniqueId(), gameMode));
            }
            case CURRENT -> this.bedrockButton((Object)builder, actions, buttonSpec, "current.", () -> this.openBedrockAdminGamemode(player, target, pageIndex));
            case NONE -> {
            }
        }
    }

    private void openBedrockGo(Player player) {
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Places")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        for (ServerId serverId : this.goTargets()) {
            this.addBedrockPlaceButton(builder, arrayList, player, serverId);
        }
        this.addBedrockPlaceBackButton(builder, arrayList, player);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockPlaceButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, ServerId targetServer) {
        BackendPlacesClickService.PlacesClick<ServerId> placesClick = this.placesClickService.action(
                this.placeLogicalSlot(targetServer),
                Ui.Shared.FORM_BACK.slot(),
                this.currentServer,
                ServerId.LOBBY,
                ServerId.SURVIVAL,
                ServerId.CREATIVE);
        switch (placesClick.action()) {
            case RETURN_SPAWN -> this.bedrockButton(builder, actions, this.placeName(targetServer), "current.", () -> this.returnServerSpawn(player));
            case TRAVEL -> this.bedrockButton(builder, actions, this.placeTargetSpec(0, targetServer), () -> {
                if (this.isServerAvailable(targetServer) || this.isPlaceWakeable(targetServer)) {
                    this.startTravel(player, targetServer);
                } else {
                    this.openBedrockGo(player);
                }
            });
            case BACK, NONE -> {
            }
        }
    }

    private void addBedrockPlaceBackButton(SimpleForm.Builder builder, List<Runnable> actions, Player player) {
        BackendPlacesClickService.PlacesClick<ServerId> placesClick = this.placesClickService.action(
                Ui.Shared.FORM_BACK.slot(),
                Ui.Shared.FORM_BACK.slot(),
                this.currentServer,
                ServerId.LOBBY,
                ServerId.SURVIVAL,
                ServerId.CREATIVE);
        if (placesClick.action() == BackendPlacesClickService.PlacesClickAction.BACK) {
            this.bedrockButton(builder, actions, Ui.Shared.FORM_BACK, () -> {
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openCubee(player);
            });
        }
    }

    private int placeLogicalSlot(ServerId serverId) {
        return switch (serverId) {
            case LOBBY -> 12;
            case SURVIVAL -> 13;
            case CREATIVE -> 14;
        };
    }

    private void openBedrockPeople(Player player, int n) {
        this.rememberPeoplePageIndex(player, n);
        List<Player> list = this.peopleNavigationService.listPeople(player);
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("People")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        int virtualSlot = 1000;
        for (Player player2 : list) {
            UUID targetId = this.peopleNavigationService.canOpenPlayer(player2) ? player2.getUniqueId() : null;
            this.addBedrockPeopleListButton(builder, arrayList, player, n, new Ui.ButtonSpec(virtualSlot++, Material.PLAYER_HEAD, player2.getName(), null), targetId, this.peopleNavigationService.itemStatus(player2));
        }
        this.addBedrockPeopleListButton(builder, arrayList, player, n, Ui.Shared.FORM_BACK, null, null);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockPeopleListButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, int pageIndex, Ui.ButtonSpec buttonSpec, UUID targetId, String lore) {
        Map<Integer, UUID> slotTargets = new HashMap<>();
        if (targetId != null) {
            slotTargets.put(buttonSpec.slot(), targetId);
        }
        BackendPeopleClickService.PeoplePageClick peopleClick = this.peopleClickService.peoplePageAction(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                Ui.People.FIND.slot(),
                slotTargets);
        switch (peopleClick.action()) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                this.switchCubeeSurface(player, CubeeSurface.HOME);
                this.openCubee(player);
            });
            case OPEN_PLAYER -> this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> {
                Player target = Bukkit.getPlayer((UUID)peopleClick.target());
                if (this.peopleNavigationService.canOpenPlayer(target)) {
                    this.openPlayer(player, target);
                } else {
                    this.openPeople(player, pageIndex);
                }
            });
            case NEXT_PAGE -> {
            }
            case NONE -> {
                if (targetId == null) {
                    this.bedrockButton((Object)builder, actions, buttonSpec, lore, () -> {});
                }
            }
        }
    }

    private void openBedrockPlayer(Player player, Player player2) {
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title(player2.getName())).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockPlayerPageButton(builder, arrayList, player, player2, Ui.People.VISIT);
        this.addBedrockPlayerPageButton(builder, arrayList, player, player2, Ui.People.INVITE);
        this.addBedrockPlayerPageButton(builder, arrayList, player, player2, Ui.People.MESSAGE);
        this.addBedrockPlayerPageButton(builder, arrayList, player, player2, Ui.Shared.FORM_BACK);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockPlayerPageButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Player target, Ui.ButtonSpec buttonSpec) {
        BackendPeopleClickService.PlayerPageAction playerAction = this.peopleClickService.playerPageAction(
                buttonSpec.slot(),
                Ui.Shared.FORM_BACK.slot(),
                Ui.People.MESSAGE.slot(),
                Ui.People.VISIT.slot(),
                Ui.People.INVITE.slot());
        switch (playerAction) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.openLastPeople(player));
            case MESSAGE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startPrivateNoteIfCurrent(player, target.getUniqueId()));
            case VISIT -> this.bedrockButton(builder, actions, buttonSpec, () -> this.createBedrockMeetRequestOrReturn(player, target.getUniqueId(), RequestKind.VISIT));
            case INVITE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.createBedrockMeetRequestOrReturn(player, target.getUniqueId(), RequestKind.INVITE));
            case NONE -> {
            }
        }
    }

    private void createBedrockMeetRequestOrReturn(Player player, UUID targetId, RequestKind requestKind) {
        Player target = Bukkit.getPlayer((UUID)targetId);
        if (!this.peopleActionService.canCreateMeetRequest(player, target)) {
            this.openLastPeople(player);
            return;
        }
        this.createRequest(player, target, requestKind);
    }

    private void openBedrockRequests(Player player, BackendMeetRequestService.RequestState<RequestKind> requestState, Player player2) {
        SimpleForm.Builder builder = (SimpleForm.Builder)SimpleForm.builder().title(requestState.kind == RequestKind.VISIT ? "Let them visit?" : "Go with them?");
        if (requestState.kind == RequestKind.VISIT) {
            builder.content(player2.getName() + "\nwants to visit you.");
        } else if (requestState.kind == RequestKind.INVITE) {
            builder.content(player2.getName() + "\ninvites you.");
        }
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        if (requestState.kind == RequestKind.VISIT) {
            this.addBedrockRequestButton(builder, arrayList, player, requestState, Ui.Requests.SURE_VISIT);
        } else if (requestState.kind == RequestKind.INVITE) {
            this.addBedrockRequestButton(builder, arrayList, player, requestState, Ui.Requests.SURE_INVITE);
        }
        this.addBedrockRequestButton(builder, arrayList, player, requestState, Ui.Requests.LATER);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockRequestButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, BackendMeetRequestService.RequestState<RequestKind> requestState, Ui.ButtonSpec buttonSpec) {
        Ui.ButtonSpec acceptSpec = requestState.kind == RequestKind.VISIT ? Ui.Requests.SURE_VISIT : Ui.Requests.SURE_INVITE;
        BackendRequestsClickService.RequestClickAction action = this.requestsClickService.action(
                buttonSpec.slot(),
                Ui.Requests.LATER.slot(),
                -1,
                acceptSpec.slot());
        switch (action) {
            case ACCEPT -> this.bedrockButton(builder, actions, buttonSpec, () -> this.acceptOrExpire(player, requestState));
            case DECLINE -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                this.declineOrExpire(player, requestState);
                FloodgateApi.getInstance().closeForm(player.getUniqueId());
            });
            case IGNORE, NONE -> {
            }
        }
    }

    private void openBedrockDrawing(Player player) {
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Sandbox")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.SET);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.WALL);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.FLOOR);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.REPLACE);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.CLONE);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.CLEAR);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.CIRCLE);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.FLIP);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.ROTATE);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.UNDO);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.REDO);
        this.addBedrockSandboxButton(builder, arrayList, player, Ui.Sandbox.BACK);
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockSandboxButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec) {
        BackendSandboxInteractionService.SandboxClickAction action = this.sandboxInteractionService.action(buttonSpec.slot());
        this.addBedrockSandboxActionButton(builder, actions, player, buttonSpec, action);
    }

    private void addBedrockSandboxActionButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, BackendSandboxInteractionService.SandboxClickAction action) {
        switch (action) {
            case BACK -> this.bedrockButton(builder, actions, buttonSpec, () -> this.leaveBedrockSandbox(player));
            case UNDO -> this.bedrockButton(builder, actions, buttonSpec, () -> this.undoDrawingIfIdle(player));
            case REDO -> this.bedrockButton(builder, actions, buttonSpec, () -> this.redoDrawingIfIdle(player));
            case SET -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.SET));
            case WALL -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.WALL));
            case FLOOR -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.FLOOR));
            case CLONE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.CLONE));
            case CLEAR -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.CLEAR));
            case CIRCLE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.CIRCLE));
            case REPLACE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.REPLACE));
            case FLIP -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.FLIP));
            case ROTATE -> this.bedrockButton(builder, actions, buttonSpec, () -> this.startDrawing(player, DrawingAction.ROTATE));
            case NONE -> {
            }
        }
    }

    private void leaveBedrockSandbox(Player player) {
        this.switchCubeeSurface(player, CubeeSurface.HOME);
        this.clearSandboxStatus(player.getUniqueId());
        this.openCubee(player);
    }

    private void openBedrockCloneConfirm(Player player) {
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Place it here?")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.PLACE, Ui.Confirm.PLACE, () -> this.placeClone(player), () -> this.cancelClonePreview(player.getUniqueId()));
        this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.CANCEL, Ui.Confirm.PLACE, () -> this.placeClone(player), () -> this.cancelClonePreview(player.getUniqueId()));
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void openBedrockClearConfirm(Player player) {
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Clear this area?")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.CLEAR, Ui.Confirm.CLEAR, () -> this.placeClear(player), () -> this.cancelClearPreview(player.getUniqueId()));
        this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.CANCEL, Ui.Confirm.CLEAR, () -> this.placeClear(player), () -> this.cancelClearPreview(player.getUniqueId()));
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void openBedrockRotateConfirm(Player player) {
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Place it turned?")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.PLACE, Ui.Confirm.PLACE, () -> this.placeRotate(player), () -> this.cancelRotatePreview(player.getUniqueId()));
        this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.CANCEL, Ui.Confirm.PLACE, () -> this.placeRotate(player), () -> this.cancelRotatePreview(player.getUniqueId()));
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void openBedrockFlipConfirm(Player player) {
        SimpleForm.Builder builder = ((SimpleForm.Builder)SimpleForm.builder().title("Place it mirrored?")).content("");
        ArrayList<Runnable> arrayList = new ArrayList<Runnable>();
        this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.PLACE, Ui.Confirm.PLACE, () -> this.placeFlip(player), () -> this.cancelFlipPreview(player.getUniqueId()));
        this.addBedrockSandboxConfirmButton(builder, arrayList, player, Ui.Confirm.CANCEL, Ui.Confirm.PLACE, () -> this.placeFlip(player), () -> this.cancelFlipPreview(player.getUniqueId()));
        this.sendBedrockForm(player, builder, arrayList);
    }

    private void addBedrockSandboxConfirmButton(SimpleForm.Builder builder, List<Runnable> actions, Player player, Ui.ButtonSpec buttonSpec, Ui.ButtonSpec confirmSpec, Runnable confirmAction, Runnable cancelAction) {
        BackendSandboxInteractionService.ConfirmAction action = this.sandboxInteractionService.confirmAction(
                buttonSpec.slot(),
                confirmSpec.slot(),
                Ui.Confirm.CANCEL.slot());
        switch (action) {
            case CONFIRM -> this.bedrockButton(builder, actions, buttonSpec, confirmAction);
            case CANCEL -> this.bedrockButton(builder, actions, buttonSpec, () -> {
                cancelAction.run();
                player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
                FloodgateApi.getInstance().closeForm(player.getUniqueId());
            });
            case NONE -> {
            }
        }
    }

    private void acceptOrExpire(Player player, BackendMeetRequestService.RequestState<RequestKind> requestState) {
        BackendMeetRequestService.RequestState<RequestKind> requestState2 = this.meetRequestService.incoming(player.getUniqueId());
        if (requestState2 == null || requestState.expired) {
            player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));
            this.clearRequests(player.getUniqueId());
            return;
        }
        if (requestState2 != requestState) {
            player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.acceptRequest(player, requestState);
    }

    private void declineOrExpire(Player player, BackendMeetRequestService.RequestState<RequestKind> requestState) {
        BackendMeetRequestService.RequestState<RequestKind> requestState2 = this.meetRequestService.incoming(player.getUniqueId());
        if (requestState2 == null || requestState.expired) {
            player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));
            this.clearRequests(player.getUniqueId());
            return;
        }
        if (requestState2 != requestState) {
            player.sendMessage((Component)Component.text((String)"too late.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        this.declineRequest(requestState);
        player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
    }

    private List<ServerId> goTargets() {
        return List.of(ServerId.LOBBY, ServerId.SURVIVAL, ServerId.CREATIVE);
    }

    private void startAvailabilityChecks() {
        this.refreshAvailability();
        this.availabilityTask = Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, this::refreshAvailability, 100L, 100L);
    }

    private void startAuraTask() {
        this.auraTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> {
            Player player;
            Object object;
            for (BackendSandboxDrawingSessionService.DrawingEntry entry : this.sandboxDrawingSessionService.drawingEntries()) {
                object = (DrawingState)entry.drawing();
                player = Bukkit.getPlayer((UUID)entry.uuid());
                if (player == null || ((DrawingState)object).first == null || ((DrawingState)object).second == null || !this.validSelection(player, (DrawingState)object)) continue;
                this.showSelection(player, (DrawingState)object);
            }
            for (BackendSandboxPreviewService.PreviewEntry entry : this.sandboxPreviewService.cloneEntries()) {
                object = (ClonePreview)entry.preview();
                player = Bukkit.getPlayer((UUID)entry.uuid());
                if (player == null) continue;
                this.showBox(player, ((ClonePreview)object).world, ((ClonePreview)object).minX, ((ClonePreview)object).minY, ((ClonePreview)object).minZ, ((ClonePreview)object).maxX, ((ClonePreview)object).maxY, ((ClonePreview)object).maxZ);
            }
            for (BackendSandboxPreviewService.PreviewEntry entry : this.sandboxPreviewService.clearEntries()) {
                object = (ClearPreview)entry.preview();
                player = Bukkit.getPlayer((UUID)entry.uuid());
                if (player == null) continue;
                this.showBox(player, ((ClearPreview)object).world, ((ClearPreview)object).minX, ((ClearPreview)object).minY, ((ClearPreview)object).minZ, ((ClearPreview)object).maxX, ((ClearPreview)object).maxY, ((ClearPreview)object).maxZ);
            }
            for (BackendSandboxPreviewService.PreviewEntry entry : this.sandboxPreviewService.rotateEntries()) {
                object = (RotatePreview)entry.preview();
                player = Bukkit.getPlayer((UUID)entry.uuid());
                if (player == null) continue;
                this.showBox(player, ((RotatePreview)object).world, ((RotatePreview)object).minX, ((RotatePreview)object).minY, ((RotatePreview)object).minZ, ((RotatePreview)object).maxX, ((RotatePreview)object).maxY, ((RotatePreview)object).maxZ);
            }
            for (BackendSandboxPreviewService.PreviewEntry entry : this.sandboxPreviewService.flipEntries()) {
                object = (RotatePreview)entry.preview();
                player = Bukkit.getPlayer((UUID)entry.uuid());
                if (player == null) continue;
                this.showBox(player, ((RotatePreview)object).world, ((RotatePreview)object).minX, ((RotatePreview)object).minY, ((RotatePreview)object).minZ, ((RotatePreview)object).maxX, ((RotatePreview)object).maxY, ((RotatePreview)object).maxZ);
            }
        }, 20L, 20L);
    }

    private void refreshAvailability() {
        this.reloadPlaces();
        this.placeAvailabilityService.refresh(this.serverAvailability, List.of(ServerId.values()), this.currentServer, this.places, serverId -> serverId.proxyName, serverId -> serverId.port);
    }

    private boolean canConnect(int n) {
        return this.placeAvailabilityService.canConnect(n);
    }

    private boolean isServerAvailable(ServerId serverId) {
        return this.placeAvailabilityService.available(this.serverAvailability, serverId, this.currentServer);
    }

    private boolean isServerReady(ServerId serverId) {
        return this.placeAvailabilityService.ready(this.places, serverId, target -> target.proxyName);
    }

    private boolean isPlaceWakeable(ServerId serverId) {
        return this.placeAvailabilityService.wakeable(this.places, serverId, target -> target.proxyName);
    }

    private void setPlaceRuntimeStatus(ServerId serverId, String string) {
        this.reloadPlaces();
        if (this.places == null) {
            return;
        }
        this.placeRuntimeStatusService.setStatus(this.places, serverId.proxyName, string);
        this.savePlaces();
    }

    private void applyWorldRules() {
        for (World world : Bukkit.getWorlds()) this.applyWorldRules(world);
    }

    private void applyWorldRules(World world) {
        this.setVerifiedGameRule(world, GameRules.SEND_COMMAND_FEEDBACK, false);
        this.setVerifiedGameRule(world, GameRules.SPAWN_MOBS, !this.worldPolicy.blockCreatureSpawns());
        this.setVerifiedGameRule(world, GameRule.DO_FIRE_TICK, !this.worldPolicy.disableFireTick());
        this.setVerifiedGameRule(world, GameRules.PVP, !this.worldPolicy.protectPlayerPvp());
        this.setVerifiedGameRule(world, GameRules.FALL_DAMAGE, !this.worldPolicy.protectFallDamage());
        if (this.currentServer == ServerId.LOBBY && world.getName().equals(this.placeSpawnWorld(ServerId.LOBBY))) {
            Location spawn = world.getSpawnLocation();
            world.getWorldBorder().setCenter(spawn.getX(), spawn.getZ());
            world.getWorldBorder().setSize(5.9999968E7);
            this.refreshLobbyFirstJoinSpawn(world);
        }
    }

    private void refreshLobbyFirstJoinSpawn(World lobbyWorld) {
        Location canonicalSpawn = lobbyWorld.getSpawnLocation();
        Location safeSpawn = this.firstJoinSpawnService.resolve(lobbyWorld);
        this.lobbyFirstJoinSpawn = safeSpawn == null ? null : safeSpawn.clone();
        if (safeSpawn == null) {
            this.getLogger().warning("First-join spawn override unavailable: Lobby world spawn has no safe position within 16 blocks above it.");
        } else if (safeSpawn.getBlockY() != canonicalSpawn.getBlockY()) {
            this.getLogger().warning("First-join spawn fallback: world=" + lobbyWorld.getName() + ", configuredY=" + canonicalSpawn.getBlockY() + ", safeY=" + safeSpawn.getBlockY());
        }
    }

    private <T> void setVerifiedGameRule(World world, GameRule<T> rule, T expected) {
        try {
            boolean applied = world.setGameRule(rule, expected);
            T actual = world.getGameRuleValue(rule);
            if (!applied || !java.util.Objects.equals(expected, actual)) {
                this.getLogger().warning("World policy verification failed: backend=" + this.currentServer.proxyName + ", world=" + world.getName() + ", rule=" + rule + ", expected=" + expected + ", actual=" + actual);
            }
        } catch (RuntimeException exception) {
            this.getLogger().log(java.util.logging.Level.WARNING, "World policy apply failed: backend=" + this.currentServer.proxyName + ", world=" + world.getName() + ", rule=" + rule + ", expected=" + expected, exception);
        }
    }

    private boolean isEnvironmentDamage(EntityDamageEvent.DamageCause damageCause) {
        return damageCause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || damageCause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || damageCause == EntityDamageEvent.DamageCause.FIRE || damageCause == EntityDamageEvent.DamageCause.FIRE_TICK || damageCause == EntityDamageEvent.DamageCause.HOT_FLOOR || damageCause == EntityDamageEvent.DamageCause.LAVA;
    }

    private void openNextTick(Runnable runnable) {
        Bukkit.getScheduler().runTaskLater((Plugin)this, runnable, 1L);
    }

    private void openRecoveryTick(Runnable runnable) {
        Bukkit.getScheduler().runTaskLater((Plugin)this, runnable, 2L);
    }

    private int pageIndex(int n, int n2) {
        int n3 = Math.max(0, (Math.max(0, n2) - 1) / PEOPLE_SLOTS.length);
        return Math.max(0, Math.min(n, n3));
    }

    private boolean hasNextPage(int n, int n2) {
        return (n + 1) * PEOPLE_SLOTS.length < n2;
    }

    private int nextLoopPage(int n, int n2) {
        if (!this.hasNextPage(0, n2)) {
            return this.pageIndex(n, n2);
        }
        int n3 = n + 1;
        return this.hasNextPage(n, n2) ? n3 : 0;
    }

    private void sendGuidance(Player player, String string, String string2) {
        player.sendMessage((Component)Component.text((String)string, (TextColor)HoneyPalette.DEFAULT_WHITE));
        player.sendMessage((Component)Component.text((String)string2, (TextColor)NamedTextColor.GRAY));
    }

    private void sendSandboxGuidance(Player player, String string, String string2) {
        this.setSandboxStatus(player, ((TextComponent)Component.text((String)string, (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.space())).append((Component)Component.text((String)string2, (TextColor)NamedTextColor.GRAY)));
    }

    private void sendSandboxStatus(Player player, String string, NamedTextColor namedTextColor) {
        if (!this.sandboxDrawingSessionService.contains(player.getUniqueId())) {
            this.clearSandboxStatus(player.getUniqueId());
        }
        if ("done.".equals(string)) {
            this.playHomeSound(player, "sandbox-done");
        }
        if ("done.".equals(string) || "try again.".equals(string) || "too large.".equals(string)) {
            TextComponent textComponent = Component.text((String)string.substring(0, string.length() - 1), (TextColor)namedTextColor);
            this.notifyActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX_NOTIFICATION, (Component)textComponent, 3000);
            return;
        }
        player.sendMessage((Component)Component.text((String)string, (TextColor)namedTextColor));
    }

    private void sendSandboxReadyStatus(Player player) {
        Component component = ((TextComponent)Component.text((String)"Ready.", (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.space())).append((Component)Component.text((String)"open cubee.", (TextColor)NamedTextColor.GRAY));
        UUID uUID = player.getUniqueId();
        if (this.sandboxStatusService.hasState(uUID)) {
            this.setSandboxStatus(player, component);
            return;
        }
        this.sandboxStatusService.remove(uUID);
        this.notifyActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX_NOTIFICATION, component, 3000);
    }

    private void sendSandboxCloneReadyStatus(Player player, int movesLeft) {
        int remaining = Math.max(0, Math.min(CLONE_RELOCATIONS, movesLeft));
        String moveStatus = remaining == 1 ? "1 move left" : remaining == 0 ? "no moves left" : remaining + " moves left";
        Component component = ((TextComponent)Component.text((String)"Ready.", (TextColor)HoneyPalette.DEFAULT_WHITE).append((Component)Component.space())).append((Component)Component.text((String)("open cubee. (" + moveStatus + ")"), (TextColor)NamedTextColor.GRAY));
        this.setSandboxStatus(player, component);
    }

    private void setSandboxStatus(Player player, Component component) {
        UUID uUID = player.getUniqueId();
        this.sandboxStatusService.set(uUID, component);
        this.publishActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX, component);
    }

    private void clearSandboxStatus(UUID uUID) {
        this.sandboxStatusService.remove(uUID);
        this.delayAtmosphereMusicActionBarResume(uUID);
        this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.SANDBOX);
        this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.SANDBOX_NOTIFICATION);
    }

    private boolean hasSandboxStatusState(UUID uUID) {
        return this.sandboxDrawingSessionService.contains(uUID) || this.sandboxPreviewService.hasAny(uUID);
    }

    private DrawingState drawingState(UUID uUID) {
        return (DrawingState)this.sandboxDrawingSessionService.get(uUID);
    }

    private void putDrawingState(UUID uUID, DrawingState drawingState) {
        this.sandboxDrawingSessionService.put(uUID, drawingState);
    }

    private boolean removeDrawingState(UUID uUID) {
        return this.sandboxDrawingSessionService.remove(uUID) != null;
    }

    private void startSandboxStatusTask() {
        this.sandboxStatusTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> this.runActionBarProducer("sandbox", () -> {
            ArrayList<UUID> arrayList = new ArrayList<UUID>();
            for (BackendSandboxStatusService.StatusEntry entry : this.sandboxStatusService.statusEntries()) {
                UUID uUID = entry.uuid();
                Player player = Bukkit.getPlayer((UUID)uUID);
                if (player == null || !player.isOnline()) {
                    arrayList.add(uUID);
                    continue;
                }
                try {
                    if (entry.component().equals(Component.empty())) {
                        this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.SANDBOX);
                    } else {
                        this.publishActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX, entry.component());
                    }
                }
                catch (RuntimeException exception) {
                    this.logActionBarFailure(player, exception);
                }
            }
            for (UUID uUID : arrayList) {
                this.clearSandboxStatus(uUID);
            }
        }), 20L, 20L);
    }

    private void activateCareOperationStatus(Player player) {
        if (!this.careWorldStatusService.shouldActivate(this.isBedrockPlayer(player))) {
            return;
        }
        this.switchCubeeRoot(player, CubeeRoot.CARE);
        UUID uUID = player.getUniqueId();
        this.careWorldStatusService.activate(uUID);
        this.updateCareWorldStatus(player);
    }

    private void clearCareWorldStatus(UUID uUID) {
        this.careWorldStatusService.clear(uUID);
        this.delayAtmosphereMusicActionBarResume(uUID);
        this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.CARE_WORLD);
    }

    private void updateCareWorldStatus(Player player) {
        UUID uUID = player.getUniqueId();
        if (!this.careWorldStatusService.contains(uUID)) {
            return;
        }
        if (this.resting() && this.restSuspendCareWorldStatus()) {
            if (this.careWorldStatusService.removeLastStatus(uUID)) this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.CARE_WORLD);
            return;
        }
        if (!player.isOnline() || this.isBedrockPlayer(player) || this.currentCubeeRoot(player) != CubeeRoot.CARE) {
            this.clearCareWorldStatus(uUID);
            return;
        }
        String string = this.careWorldRawStatus();
        String string2 = this.careWorldActionBarStatus(string);
        String string3 = this.careWorldChatStatus(string);
        if (this.travelStateService.contains(uUID) || this.sandboxStatusService.contains(uUID)) {
            String string4 = this.careWorldStatusService.takeLastStatus(uUID);
            if (this.careWorldActionBarStatus(string4) != null) this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.CARE_WORLD);
            return;
        }
        if (string3 != null) {
            String string5 = this.careWorldStatusService.rememberStatus(uUID, string);
            if (!string.equals(string5)) {
                player.sendMessage((Component)Component.text((String)string3, (TextColor)NamedTextColor.GRAY));
            }
            if (this.careWorldActionBarStatus(string5) != null) this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.CARE_WORLD);
            return;
        }
        if (string2 == null) {
            String string6 = this.careWorldStatusService.takeLastStatus(uUID);
            if (this.careWorldActionBarStatus(string6) != null) this.clearActionBar(uUID, BackendActionBarCoordinator.Owner.CARE_WORLD);
            return;
        }
        this.careWorldStatusService.rememberStatus(uUID, string);
        this.publishActionBar(player, BackendActionBarCoordinator.Owner.CARE_WORLD, Component.text((String)string2, (TextColor)NamedTextColor.GRAY));
    }

    private String careWorldRawStatus() {
        if (this.backupInProgress(this.currentServer)) {
            return "keeping a copy.";
        }
        return this.chunkStatus();
    }

    private String careWorldActionBarStatus(String string) {
        if (string == null) {
            return null;
        }
        return switch (string) {
            case "keeping a copy." -> "keeping a copy";
            case "waiting." -> "waiting";
            case "running." -> "running";
            case "idle." -> null;
            default -> string.endsWith("% ready") ? string : null;
        };
    }

    private String careWorldChatStatus(String string) {
        if ("done.".equals(string) || "unavailable.".equals(string)) {
            return string;
        }
        return null;
    }

    private void startCareWorldStatusTask() {
        this.careWorldStatusTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, () -> this.runActionBarProducer("care-world", () -> {
            for (UUID uUID : this.careWorldStatusService.viewerSnapshot()) {
                Player player = Bukkit.getPlayer((UUID)uUID);
                if (player == null) {
                    this.clearCareWorldStatus(uUID);
                    continue;
                }
                try {
                    this.updateCareWorldStatus(player);
                }
                catch (RuntimeException exception) {
                    this.logActionBarFailure(player, exception);
                }
            }
        }), 20L, 20L);
    }

    private void startTabTask() {
        if (this.tabTask != null) {
            this.tabTask.cancel();
            this.tabTask = null;
        }
        if (!this.configBoolean("tab.enabled", true)) {
            return;
        }
        this.configureTabTime();
        this.updateTabs();
        this.scheduleNextTabUpdate();
    }

    private void scheduleNextTabUpdate() {
        long configuredTicks = (long)this.configInt("tab.update-ticks", 1200, 5, 1200);
        long delayTicks = configuredTicks;
        if (configuredTicks == 1200L) {
            long millisUntilNextMinute = 60000L - Math.floorMod(System.currentTimeMillis(), 60000L);
            delayTicks = Math.max(1L, (millisUntilNextMinute + 49L) / 50L);
        }
        this.tabTask = Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            this.updateTabs();
            if (this.isEnabled() && this.configBoolean("tab.enabled", true)) {
                this.scheduleNextTabUpdate();
            }
        }, delayTicks);
    }

    private void updateTabs() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.updateTab(player);
        }
    }

    private void updateTab(Player player) {
        if (player == null || !player.isOnline() || !this.configBoolean("tab.enabled", true)) return;
        try {
            player.sendPlayerListHeaderAndFooter(this.tabComponent(player, "tab.header.lines", this.defaultTabHeaderLines()), this.tabComponent(player, "tab.footer.lines", this.defaultTabFooterLines()));
        }
        catch (RuntimeException runtimeException) {
            this.getLogger().warning("Unable to update LemonOS TAB for " + player.getName() + ": " + runtimeException.getMessage());
        }
    }

    private void configureTabTime() {
        String zone = this.configString("tab.time.zone", "Asia/Bangkok");
        String format = this.configString("tab.time.format", "EEEdd HH:mm");
        try {
            this.tabTimeZone = ZoneId.of(zone);
        }
        catch (RuntimeException exception) {
            this.tabTimeZone = ZoneId.of("Asia/Bangkok");
            this.getLogger().warning("Invalid tab.time.zone '" + zone + "'; using Asia/Bangkok.");
        }
        try {
            this.tabTimeFormatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
        }
        catch (RuntimeException exception) {
            this.tabTimeFormatter = DateTimeFormatter.ofPattern("EEEdd HH:mm", Locale.ENGLISH);
            this.getLogger().warning("Invalid tab.time.format '" + format + "'; using EEEdd HH:mm.");
        }
    }

    private List<String> defaultTabHeaderLines() {
        return List.of("<#FAF9F6>Honey", "<#C9D8B6>green tea", "");
    }

    private List<String> defaultTabFooterLines() {
        return List.of("", "<gray>%server%", "<gray>%time%");
    }

    private Component tabComponent(Player player, String string, List<String> list) {
        List<String> list2 = this.configStringList(string, list);
        TextComponent.Builder builder = Component.text();
        boolean bl = true;
        for (String string2 : list2) {
            if (!bl) {
                builder.append(Component.newline());
            }
            builder.append(this.tabLine(player, string2 == null ? "" : string2));
            bl = false;
        }
        return builder.build();
    }

    private Component tabLine(Player player, String string) {
        String string2 = this.applyTabPlaceholders(player, string);
        try {
            return MiniMessage.miniMessage().deserialize(string2);
        }
        catch (RuntimeException runtimeException) {
            return Component.text(string2, NamedTextColor.GRAY);
        }
    }

    private String applyTabPlaceholders(Player player, String string) {
        Location location = player == null ? null : player.getLocation();
        String string2 = location == null ? "" : Integer.toString(location.getBlockX());
        String string3 = location == null ? "" : Integer.toString(location.getBlockY());
        String string4 = location == null ? "" : Integer.toString(location.getBlockZ());
        String string5 = location == null ? "" : this.cardinalDirection(location);
        String string6 = location == null ? "" : this.blockCoords(location);
        return string.replace("%server%", this.currentServer == null ? "" : this.currentServer.label)
                .replace("%server_id%", this.currentServer == null ? "" : this.currentServer.proxyName)
                .replace("%player%", player == null ? "" : player.getName())
                .replace("%players_online%", Integer.toString(Bukkit.getOnlinePlayers().size()))
                .replace("%time%", this.tabTimeFormatter.format(ZonedDateTime.now(this.tabTimeZone)))
                .replace("%direction%", string5)
                .replace("%x%", string2)
                .replace("%y%", string3)
                .replace("%z%", string4)
                .replace("%xyz%", string6)
                .replace("%direction_xyz%", location == null ? "" : this.facingBlockCoords(location));
    }

    private void startBoardTask() {
        this.boardLifecycleService.stop();
        BackendBoardOrchestrationService.Plan plan = this.boardOrchestrationService.plan(this.currentServer.proxyName, this.boardConfig, BOARD_DEFINITIONS);
        if (plan.clearStayedClose()) this.clearStayedCloseDisplays();
        for (String rolePrefix : plan.disabledRolePrefixes()) this.clearBoardDisplayPrefix(rolePrefix);
        if (!plan.active()) {
            this.boardChunkLeaseService.releaseAll();
            return;
        }
        this.boardLifecycleService.start(40L, plan.periodTicks(), this::updateBoards);
    }

    private void updateBoards() {
        this.boardChunkLeaseService.beginCycle();
        try {
            if (this.currentServer == ServerId.LOBBY && this.boardConfig.enabled(BackendBoardConfig.STAYED_CLOSE)) this.updateStayedCloseDisplay();
            this.updateMetricBoards();
        } finally {
            this.boardChunkLeaseService.endCycle();
        }
    }

    private void updateMetricBoards() {
        if (this.hudDataFile != null && this.hudDataFile.isFile()) {
            synchronized (this.hudIoLock) {
                this.hudData = this.yamlStore.load(this.hudDataFile);
                this.ensureHudDataDefaults();
            }
        }
        for (BackendBoardDefinition boardDefinition : BOARD_DEFINITIONS) {
            if (boardDefinition.enabledOn(this.currentServer.proxyName)) {
                this.updateMetricBoard(boardDefinition);
                continue;
            }
            this.clearBoardDisplayPrefix(boardDefinition.rolePrefix());
        }
    }

    private void updateMetricBoard(BackendBoardDefinition boardDefinition) {
        String string = boardDefinition.dataKey();
        String string2 = boardDefinition.rolePrefix();
        String string5 = boardDefinition.configPath();
        if (!this.boardConfig.enabled(boardDefinition.dataKey())) {
            this.clearBoardDisplayPrefix(string2);
            return;
        }
        BackendDisplayPlacementService.Placement placement = this.displayPlacementService.hudPlacement(this.backendDisplayConfig(), string5, this.placeSpawnWorld(this.currentServer));
        World world = Bukkit.getWorld((String)placement.worldName());
        if (world == null) {
            return;
        }
        Location location = this.backendDisplayLocation(world, placement);
        this.forceLoadBackendDisplayLocation(world, location);
        this.clearDisplayBoard(world, location, role -> role != null && role.startsWith(string2));
        int n = this.boardConfig.top(boardDefinition.dataKey());
        List<HudRank> list = this.readHudTop(string, n);
        ArrayList<BackendHudDisplayService.Rank> arrayList = new ArrayList<BackendHudDisplayService.Rank>();
        for (HudRank hudRank : list) {
            arrayList.add(new BackendHudDisplayService.Rank(hudRank.name, hudRank.score));
        }
        BackendHudDisplayService.Board board = new BackendHudDisplayService.Board(string5, string2, boardDefinition.defaultTitle(), boardDefinition.defaultSubtitle(), boardDefinition.defaultBottomLine());
        BackendDisplayModel displayModel = this.hudDisplayService.model(this.backendDisplayConfig(), board, arrayList);
        for (BackendDisplayModel.Entry entry : displayModel.entries()) {
            this.updateBoardDisplayRole(world, this.stayedCloseLocation(location, entry.offsetX(), entry.offsetY(), entry.offsetZ()), entry.role(), string5, this.backendDisplayComponent(entry), this.backendDisplayAlignment(entry.alignment()));
        }
    }

    private BackendDisplayConfig backendDisplayConfig() {
        return this.boardConfig;
    }

    private Component backendDisplayComponent(BackendDisplayModel.Entry entry) {
        return Component.text((String)entry.text(), (TextColor)(entry.colorRole() == BackendDisplayModel.ColorRole.WHITE ? HoneyPalette.DEFAULT_WHITE : NamedTextColor.GRAY));
    }

    private TextDisplay.TextAlignment backendDisplayAlignment(BackendDisplayModel.Alignment alignment) {
        if (alignment == BackendDisplayModel.Alignment.LEFT) {
            return TextDisplay.TextAlignment.LEFT;
        }
        if (alignment == BackendDisplayModel.Alignment.RIGHT) {
            return TextDisplay.TextAlignment.RIGHT;
        }
        return TextDisplay.TextAlignment.CENTER;
    }

    private void updateStayedCloseDisplay() {
        if (this.currentServer != ServerId.LOBBY || !this.boardConfig.enabled(BackendBoardConfig.STAYED_CLOSE)) {
            return;
        }
        BackendDisplayPlacementService.Placement placement = this.displayPlacementService.stayedClosePlacement(this.backendDisplayConfig(), this.placeSpawnWorld(ServerId.LOBBY));
        World world = Bukkit.getWorld((String)placement.worldName());
        if (world == null) {
            return;
        }
        Location location = this.backendDisplayLocation(world, placement);
        this.forceLoadBackendDisplayLocation(world, location);
        this.clearDisplayBoard(world, location, role -> role != null && (this.isStayedCloseDisplayRole(role) || this.isLegacyStayedCloseDisplayRole(role)));
        List<StayedCloseRank> list = this.readStayedCloseTop();
        ArrayList<BackendStayedCloseDisplayService.Rank> arrayList = new ArrayList<BackendStayedCloseDisplayService.Rank>();
        for (StayedCloseRank stayedCloseRank : list) {
            arrayList.add(new BackendStayedCloseDisplayService.Rank(stayedCloseRank.name, stayedCloseRank.totalSeconds));
        }
        BackendDisplayModel displayModel = this.stayedCloseDisplayService.model(this.backendDisplayConfig(), arrayList);
        if (!displayModel.bedrockEnabled()) {
            this.clearStayedCloseBedrockDisplays();
        }
        for (BackendDisplayModel.Entry entry : displayModel.entries()) {
            this.updateStayedCloseDisplayRole(world, this.stayedCloseLocation(location, entry.offsetX(), entry.offsetY(), entry.offsetZ()), entry.role(), this.backendDisplayComponent(entry), this.backendDisplayAlignment(entry.alignment()));
        }
    }

    private Display.Billboard stayedCloseBillboard() {
        String string = this.boardConfig.stringValue("boards.stayed-close.display.billboard", "fixed").trim().toLowerCase(Locale.ROOT);
        if ("center".equals(string)) {
            return Display.Billboard.CENTER;
        }
        if ("vertical".equals(string)) {
            return Display.Billboard.VERTICAL;
        }
        if ("horizontal".equals(string)) {
            return Display.Billboard.HORIZONTAL;
        }
        return Display.Billboard.FIXED;
    }

    private Location stayedCloseLocation(Location location, double d, double d2, double d3) {
        Location location2 = location.clone();
        location2.add(d, d2, d3);
        return location2;
    }

    private Location backendDisplayLocation(World world, BackendDisplayPlacementService.Placement placement) {
        Location location = new Location(world, placement.x(), placement.y(), placement.z());
        location.setYaw(placement.yaw());
        location.setPitch(placement.pitch());
        return location;
    }

    private void forceLoadBackendDisplayLocation(World world, Location location) {
        this.boardChunkLeaseService.acquire(world, location.getBlockX() >> 4, location.getBlockZ() >> 4);
        if (!world.getChunkAt(location).isLoaded()) {
            world.getChunkAt(location).load();
        }
    }

    private void updateStayedCloseDisplayRole(World world, Location location, String string, Component component, TextDisplay.TextAlignment textAlignment) {
        TextDisplay textDisplay = this.findStayedCloseDisplay(world, string);
        if (textDisplay == null) {
            Entity entity = world.spawnEntity(location, EntityType.TEXT_DISPLAY);
            if (!(entity instanceof TextDisplay)) {
                entity.remove();
                return;
            }
            textDisplay = (TextDisplay)entity;
            textDisplay.getPersistentDataContainer().set(this.stayedCloseDisplayKey, PersistentDataType.STRING, string);
        } else {
            textDisplay.teleport(location);
        }
        boolean bl = this.isBedrockStayedCloseRole(string);
        boolean bl2 = STAYED_CLOSE_BOTTOM_DISPLAY.equals(string);
        boolean bl3 = STAYED_CLOSE_BEDROCK_BOTTOM_DISPLAY.equals(string);
        this.applyBackendDisplayEntity(textDisplay, component, textAlignment, this.displayEntityService.stayedCloseStyle(this.backendDisplayConfig(), string, bl, bl2, bl3));
        this.updateStayedCloseDisplayVisibility(textDisplay, string);
    }

    private void updateBoardDisplayRole(World world, Location location, String string, String string2, Component component, TextDisplay.TextAlignment textAlignment) {
        TextDisplay textDisplay = this.findHudDisplay(world, string);
        if (textDisplay == null) {
            Entity entity = world.spawnEntity(location, EntityType.TEXT_DISPLAY);
            if (!(entity instanceof TextDisplay)) {
                entity.remove();
                return;
            }
            textDisplay = (TextDisplay)entity;
            textDisplay.getPersistentDataContainer().set(this.stayedCloseDisplayKey, PersistentDataType.STRING, string);
        } else {
            textDisplay.teleport(location);
        }
        this.applyBackendDisplayEntity(textDisplay, component, textAlignment, this.displayEntityService.hudStyle(this.backendDisplayConfig(), string2, string));
        this.updateBoardDisplayVisibility(textDisplay, string, string2);
    }

    private void applyBackendDisplayEntity(TextDisplay textDisplay, Component component, TextDisplay.TextAlignment textAlignment, BackendDisplayEntityService.EntityStyle entityStyle) {
        textDisplay.text(component);
        textDisplay.setBillboard(this.stayedCloseBillboard());
        textDisplay.setAlignment(textAlignment);
        textDisplay.setShadowed(false);
        textDisplay.setSeeThrough(false);
        textDisplay.setDefaultBackground(false);
        float f = entityStyle.scale();
        textDisplay.setTransformation(new Transformation(new Vector3f(0.0f, 0.0f, 0.0f), new AxisAngle4f(), new Vector3f(f, f, f), new AxisAngle4f()));
        textDisplay.setBackgroundColor(Color.fromARGB(entityStyle.backgroundAlpha(), 18, 22, 26));
        textDisplay.setViewRange((float)entityStyle.viewRange());
        textDisplay.setLineWidth(entityStyle.lineWidth());
        textDisplay.setPersistent(true);
    }

    private TextDisplay findHudDisplay(World world, String string) {
        return this.displayBoardLifecycleService.findUniqueDisplay(world, string, this::displayRole);
    }

    private void clearBoardDisplayPrefix(String string) {
        this.displayBoardLifecycleService.clearDisplays(Bukkit.getWorlds(), this::displayRole, role -> role.startsWith(string));
    }

    private void clearDisplayBoard(World world, Location location, Predicate<String> predicate) {
        this.displayBoardLifecycleService.clearDisplayBoard(world, location, this::displayRole, predicate, this::isNearDisplayBase);
    }

    private String displayRole(TextDisplay textDisplay) {
        return this.currentDisplayRole(textDisplay);
    }

    private String currentDisplayRole(TextDisplay textDisplay) {
        return textDisplay.getPersistentDataContainer().get(this.stayedCloseDisplayKey, PersistentDataType.STRING);
    }

    private boolean isNearDisplayBase(TextDisplay textDisplay, Location location) {
        Location location2 = textDisplay.getLocation();
        return location2.getWorld() != null && location.getWorld() != null && location2.getWorld().equals(location.getWorld()) && location2.distanceSquared(location) <= DISPLAY_BOARD_CLEAR_RADIUS_SQUARED;
    }

    private void updateBoardDisplayVisibility(TextDisplay textDisplay, String string, String string2) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.displayVisibilityService.hudVisible(string, this.boardConfig.bedrockEnabledAtPath(string2), this.isBedrockPlayer(player))) {
                player.hideEntity((Plugin)this, (Entity)textDisplay);
                continue;
            }
            player.showEntity((Plugin)this, (Entity)textDisplay);
        }
    }

    private void updateStayedCloseDisplayVisibility(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        for (World world : Bukkit.getWorlds()) {
            for (TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class)) {
                String string = textDisplay.getPersistentDataContainer().get(this.stayedCloseDisplayKey, PersistentDataType.STRING);
                if (string != null) {
                    this.updateStayedCloseDisplayVisibility(player, textDisplay, string);
                }
            }
        }
    }

    private void updateStayedCloseDisplayVisibility(TextDisplay textDisplay, String string) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.updateStayedCloseDisplayVisibility(player, textDisplay, string);
        }
    }

    private void updateStayedCloseDisplayVisibility(Player player, Entity entity, String string) {
        if (!this.displayVisibilityService.stayedCloseVisible(string, this.boardConfig.bedrockEnabled(BackendBoardConfig.STAYED_CLOSE), this.isBedrockPlayer(player))) {
            player.hideEntity((Plugin)this, entity);
            return;
        }
        player.showEntity((Plugin)this, entity);
    }

    private boolean isBedrockStayedCloseRole(String string) {
        return this.displayVisibilityService.isStayedCloseBedrockRole(string);
    }

    private boolean isStayedCloseDisplayRole(String string) {
        return STAYED_CLOSE_TITLE_DISPLAY.equals(string) || STAYED_CLOSE_SUBTITLE_DISPLAY.equals(string) || STAYED_CLOSE_BOTTOM_DISPLAY.equals(string) || string != null && (string.startsWith(STAYED_CLOSE_NAME_DISPLAY) || string.startsWith(STAYED_CLOSE_TIME_DISPLAY) || this.isBedrockStayedCloseRole(string));
    }

    private TextDisplay findStayedCloseDisplay(World world, String string) {
        return this.displayBoardLifecycleService.findUniqueDisplay(world, string, this::displayRole);
    }

    private boolean isLegacyStayedCloseDisplayRole(String string) {
        return STAYED_CLOSE_DISPLAY.equals(string) || STAYED_CLOSE_HEADER_DISPLAY.equals(string) || STAYED_CLOSE_NAMES_DISPLAY.equals(string) || STAYED_CLOSE_TIMES_DISPLAY.equals(string);
    }

    private void clearStayedCloseDisplays() {
        this.displayBoardLifecycleService.clearDisplays(Bukkit.getWorlds(), this::displayRole, role -> this.isStayedCloseDisplayRole(role) || this.isLegacyStayedCloseDisplayRole(role));
    }

    private void clearStayedCloseBedrockDisplays() {
        this.displayBoardLifecycleService.clearDisplays(Bukkit.getWorlds(), this::currentDisplayRole, this::isBedrockStayedCloseRole);
    }

    private List<StayedCloseRank> readStayedCloseTop() {
        File file = this.runtimeLayout.dataFile("playtime.yml");
        ArrayList<StayedCloseRank> arrayList = new ArrayList<StayedCloseRank>();
        int n = this.boardConfig.top(BackendBoardConfig.STAYED_CLOSE);
        for (BackendStayedClosePlaytimeService.Rank rank : this.stayedClosePlaytimeService.top(file, n)) {
            arrayList.add(new StayedCloseRank(rank.name(), rank.totalSeconds()));
        }
        return arrayList;
    }

    private List<HudRank> readHudTop(String string, int n) {
        ArrayList<HudRank> arrayList = new ArrayList<HudRank>();
        for (BackendHudDataService.Rank rank : this.hudDataService.top(this.hudData, string, n)) {
            arrayList.add(new HudRank(rank.name(), rank.score()));
        }
        return arrayList;
    }

    private void startRestTask() {
        this.markRestEmptyIfNeeded();
        this.restTask = Bukkit.getScheduler().runTaskTimer((Plugin)this, this::tickRest, 20L, 600L);
    }

    private void tickRest() {
        if (!this.restEnabledHere()) {
            this.restoreRestPlaceStatus();
            this.restStateService.reset();
            return;
        }
        long l = System.currentTimeMillis();
        BackendRestStateService.RestAction restAction = this.restStateService.tick(l, Bukkit.getOnlinePlayers().isEmpty(), this.restIdleMinutes(), this.restAutoStop(), this.restSleepMinutes());
        if (restAction == BackendRestStateService.RestAction.WAKE) {
            this.scheduleRestWake();
        } else if (restAction == BackendRestStateService.RestAction.ENTER_REST) {
            this.enterRest();
        } else if (restAction == BackendRestStateService.RestAction.SLEEP) {
            this.sleepNow();
        }
    }

    private void markRestEmptyIfNeeded() {
        if (!this.restEnabledHere()) {
            return;
        }
        this.restStateService.markEmptyIfNeeded(System.currentTimeMillis(), Bukkit.getOnlinePlayers().isEmpty());
    }

    private void wakeFromRestIfNeeded() {
        if (!this.restEnabledHere()) {
            return;
        }
        if (this.restStateService.wakeFromRestIfNeeded() == BackendRestStateService.RestAction.WAKE) {
            this.scheduleRestWake();
        }
    }

    private void enterRest() {
        if (!this.restEnabledHere() || !this.restStateService.canEnterRest()) {
            return;
        }
        this.capturePreRestPlaceStatus();
        this.restStateService.enterRest(System.currentTimeMillis());
        this.writeRestingSince();
        this.setCurrentPlaceRuntimeStatus(this.restRestingStatus());
    }

    private void sleepNow() {
        this.restStateService.markSleeping();
        this.setCurrentPlaceRuntimeStatus(this.restSleepStatus());
        this.clearRestingSince();
        this.prepareLiveBackupSnapshot();
        Bukkit.shutdown();
    }

    private void scheduleRestWake() {
        this.setCurrentPlaceRuntimeStatus(this.restWakingStatus());
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            if (!this.restStateService.isWakingUp()) {
                return;
            }
            this.restoreRestPlaceStatus();
            this.restStateService.finishWake();
        }, (long)this.restWakeDelaySeconds() * 20L);
    }

    private void capturePreRestPlaceStatus() {
        this.reloadPlaces();
        this.placeRuntimeStatusService.capturePreRestStatus(this.places, this.currentServer.proxyName, this.restStateService, this.restRestingStatus(), this.restWakingStatus());
    }

    private void restoreRestPlaceStatus() {
        if (this.currentServer != ServerId.SURVIVAL && this.currentServer != ServerId.CREATIVE) {
            return;
        }
        if (this.restStateService.isSleeping()) {
            return;
        }
        this.reloadPlaces();
        if (this.places == null) {
            return;
        }
        String string2 = this.placeRuntimeStatusService.restoreRestStatus(this.places, this.currentServer.proxyName, this.restStateService, this.restRestingStatus(), this.restWakingStatus());
        if (string2 == null) {
            return;
        }
        this.setCurrentPlaceRuntimeStatus(string2);
        this.clearRestingSince();
    }

    private void setCurrentPlaceRuntimeStatus(String string) {
        this.reloadPlaces();
        if (this.places == null) {
            return;
        }
        this.placeRuntimeStatusService.setStatus(this.places, this.currentServer.proxyName, string);
        this.savePlaces();
    }

    private void writeRestingSince() {
        if (this.currentServer != ServerId.SURVIVAL && this.currentServer != ServerId.CREATIVE) {
            return;
        }
        try {
            this.restFileService.writeRestingSince(this.honeyRoot(), this.currentServer.proxyName, this.restStateService.restSinceMillis(System.currentTimeMillis()));
        }
        catch (IOException iOException) {
            this.getLogger().warning("Unable to write LemonOS rest timestamp for " + this.currentServer.proxyName + ": " + iOException.getMessage());
        }
    }

    private void clearRestingSince() {
        if (this.currentServer != ServerId.SURVIVAL && this.currentServer != ServerId.CREATIVE) {
            return;
        }
        try {
            this.restFileService.clearRestingSince(this.honeyRoot(), this.currentServer.proxyName);
        }
        catch (IOException iOException) {
            this.getLogger().warning("Unable to clear LemonOS rest timestamp for " + this.currentServer.proxyName + ": " + iOException.getMessage());
        }
    }

    private boolean restEnabledHere() {
        return this.configBoolean("rest.enabled", true) && (this.currentServer == ServerId.SURVIVAL || this.currentServer == ServerId.CREATIVE);
    }

    private boolean resting() {
        return this.restStateService.isResting();
    }

    private int restIdleMinutes() {
        return this.configInt("rest.idle-minutes", 5, 1, 1440);
    }

    private boolean restAutoStop() {
        return this.configBoolean("rest.auto-stop", true);
    }

    private int restSleepMinutes() {
        return this.configInt("rest.sleep-minutes", 30, 1, 1440);
    }

    private int restWakeDelaySeconds() {
        return this.configInt("rest.wake-delay-seconds", 2, 0, 120);
    }

    private String restRestingStatus() {
        return this.configString("rest.status.resting", "resting.");
    }

    private String restWakingStatus() {
        return this.configString("rest.status.waking-up", "waking up.");
    }

    private String restSleepStatus() {
        return this.configString("rest.status.sleep", "sleep.");
    }

    private boolean restSuspendAtmosphere() {
        return this.configBoolean("rest.suspend.atmosphere", true);
    }

    private boolean restSuspendCareWorldStatus() {
        return this.configBoolean("rest.suspend.care-world-status", true);
    }

    private void startAtmosphereTask() {
        this.atmosphereLifecycleService.stop();
        BackendAtmosphereOrchestrationService.Schedule schedule = this.atmosphereOrchestrationService.schedule(this.currentServer == ServerId.SURVIVAL, this.atmosphereConfig);
        if (!schedule.active()) return;
        this.atmosphereLifecycleService.start(schedule.initialDelayTicks(), schedule.periodTicks(), () -> this.runActionBarProducer("atmosphere", this::tickAtmosphere));
    }

    private void tickAtmosphere() {
        if (!this.atmosphereOrchestrationService.shouldTick(this.atmosphereConfig, this.resting(), this.restSuspendAtmosphere())) return;
        long l = this.monotonicMillis();
        for (World world : Bukkit.getWorlds()) {
            String string = world.getUID().toString();
            TimePhase timePhase = this.timePhase(world);
            WeatherPhase weatherPhase = this.weatherPhase(world);
            BackendAtmosphereWorldService.PhaseChange phaseChange = this.atmosphereWorldService.observe(string, timePhase, weatherPhase);
            if (phaseChange.timeChanged()) {
                this.broadcastAtmosphere(world, "time." + timePhase.key, l);
            }
            if (phaseChange.weatherChanged()) {
                this.broadcastAtmosphere(world, "weather." + weatherPhase.key, l);
            }
        }
        this.showPeriodicAtmosphere(l);
        this.tickActivitySessions(l);
        this.repeatAtmosphere(l);
        this.tickAtmosphereMusic(l);
    }

    private void showPeriodicAtmosphere(long l) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.canShowAtmosphere(player)) {
                continue;
            }
            if (!this.atmosphereActionBarService.periodicReady(player.getUniqueId(), l, this.atmosphereCooldownSeconds())) {
                continue;
            }
            World world = player.getWorld();
            WeatherPhase weatherPhase = this.weatherPhase(world);
            if (weatherPhase != WeatherPhase.CLEAR) {
                this.showAtmosphere(player, "weather." + weatherPhase.key, l);
                continue;
            }
            this.showAtmosphere(player, "time." + this.timePhase(world).key, l);
        }
    }

    private void broadcastAtmosphere(World world, String string, long l) {
        for (Player player : world.getPlayers()) {
            this.showAtmosphere(player, string, l);
        }
    }

    private void repeatAtmosphere(long l) {
        for (BackendAtmosphereActionBarService.RepeatMessage repeatMessage : this.atmosphereActionBarService.repeatMessages(l, this.atmosphereRepeatSeconds())) {
            Player player = Bukkit.getPlayer((UUID)repeatMessage.uuid());
            if (player == null || !this.canShowAtmosphere(player)) {
                continue;
            }
            this.atmosphereActionBarService.markRepeated(repeatMessage.uuid(), l);
        }
    }

    private void tickAtmosphereMusic(long l) {
        if (!this.atmosphereMusicEnabled() || this.currentServer != ServerId.LOBBY) {
            this.stopAllAtmosphereMusic();
            return;
        }
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.canPlayAtmosphereMusic(player)) {
                this.stopAtmosphereMusic(player);
                continue;
            }
            arrayList.add(player);
        }
        if (arrayList.isEmpty()) {
            this.stopAllAtmosphereMusic();
            return;
        }
        this.refreshAtmosphereMusicActionBars(arrayList, l);
        if (this.atmosphereMusicService.waitingForNextTrack(l, this.atmosphereMusicDelaySeconds())) {
            return;
        }
        BackendAtmosphereMusicService.Track atmosphereTrack = this.atmosphereMusicService.pickTrack(this.atmosphereMusicGroups());
        if (atmosphereTrack == null) {
            this.atmosphereMusicService.markNoTrack(l);
            return;
        }
        this.stopAllAtmosphereMusicSounds(arrayList);
        boolean bl = false;
        for (Player player : arrayList) {
            bl |= this.playAtmosphereMusic(player, atmosphereTrack);
        }
        this.atmosphereMusicService.finishTrackAttempt(atmosphereTrack, l, this.atmosphereMusicGapSeconds(), bl);
    }

    private List<BackendAtmosphereMusicService.Group> atmosphereMusicGroups() {
        ArrayList<BackendAtmosphereMusicService.Group> groups = new ArrayList<BackendAtmosphereMusicService.Group>();
        for (String group : List.of("warm", "playful", "special")) {
            ArrayList<BackendAtmosphereMusicService.Track> tracks = new ArrayList<BackendAtmosphereMusicService.Track>();
            for (String track : this.atmosphereMusicTracks(group)) {
                tracks.add(new BackendAtmosphereMusicService.Track(track, this.normalizeAtmosphereMusicSound(track), group, this.atmosphereMusicTrackSeconds(track)));
            }
            groups.add(new BackendAtmosphereMusicService.Group(group, this.atmosphereMusicWeight(group), tracks));
        }
        return groups;
    }

    private List<String> atmosphereMusicTracks(String string) {
        if (this.config == null) {
            return List.of();
        }
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string2 : this.config.getStringList("atmosphere.music.tracks." + string)) {
            if (string2 == null || string2.isBlank()) continue;
            arrayList.add(string2.trim());
        }
        return arrayList;
    }

    private boolean playAtmosphereMusic(Player player, BackendAtmosphereMusicService.Track atmosphereTrack) {
        float f = (float)this.atmosphereConfig.musicVolume();
        float f2 = (float)this.atmosphereConfig.musicPitch();
        if (f <= 0.0f || atmosphereTrack.sound() == null || atmosphereTrack.sound().isBlank()) {
            return false;
        }
        try {
            player.playSound((Entity)player, atmosphereTrack.sound(), f, f2);
            this.showAtmosphereMusicActionBar(player, atmosphereTrack);
            return true;
        }
        catch (RuntimeException runtimeException) {
            this.getLogger().warning("LemonOS atmosphere music '" + atmosphereTrack.sound() + "' is unavailable.");
            return false;
        }
    }

    private void showAtmosphereMusicActionBar(Player player, BackendAtmosphereMusicService.Track atmosphereTrack) {
        if (player == null || !player.isOnline() || atmosphereTrack == null || !this.atmosphereConfig.musicActionBarEnabled() || !this.canShowAtmosphereMusicActionBar(player)) {
            return;
        }
        String string = this.atmosphereConfig.musicActionBarFormat();
        if (string == null || string.isBlank()) {
            string = "playing {music}";
        }
        String string2 = this.atmosphereMusicDisplayName(atmosphereTrack);
        this.publishActionBar(player, BackendActionBarCoordinator.Owner.MUSIC, Component.text((String)string.replace("{music}", string2), (TextColor)TextColor.color((int)11184810)));
    }

    private void refreshAtmosphereMusicActionBars(List<Player> list, long l) {
        BackendAtmosphereMusicService.Track atmosphereTrack = this.atmosphereMusicService.refreshActionBarTrack(l, this.atmosphereMusicActionBarRefreshTicks());
        if (atmosphereTrack == null) {
            return;
        }
        for (Player player : list) {
            this.showAtmosphereMusicActionBar(player, atmosphereTrack);
        }
    }

    private boolean canShowAtmosphereMusicActionBar(Player player) {
        return player != null && this.canPlayAtmosphereMusic(player) && !this.atmosphereMusicActionBarResumeDelayed(player.getUniqueId());
    }

    private boolean atmosphereMusicActionBarResumeDelayed(UUID uUID) {
        return this.atmosphereMusicService.actionBarResumeDelayed(uUID, this.monotonicMillis());
    }

    private void delayAtmosphereMusicActionBarResume(UUID uUID) {
        if (uUID == null || this.currentServer != ServerId.LOBBY) {
            return;
        }
        this.atmosphereMusicService.delayActionBarResume(uUID, this.monotonicMillis(), this.atmosphereMusicActionBarResumeDelayTicks());
    }

    private String atmosphereMusicDisplayName(BackendAtmosphereMusicService.Track atmosphereTrack) {
        if (atmosphereTrack == null || atmosphereTrack.key() == null || atmosphereTrack.key().isBlank()) {
            return "music";
        }
        String string = atmosphereTrack.key().trim();
        String string2 = this.atmosphereConfig.musicDisplayName(string, this.defaultAtmosphereMusicDisplayName(string));
        if (string2 != null && !string2.isBlank()) {
            return string2.trim();
        }
        return this.defaultAtmosphereMusicDisplayName(string);
    }

    private String defaultAtmosphereMusicDisplayName(String string) {
        String string2 = string == null ? "" : string.trim();
        if (string2.isBlank()) {
            return "music";
        }
        switch (string2.toUpperCase(Locale.ROOT)) {
            case "MUSIC_DISC_CAT": {
                return "C418 - cat";
            }
            case "MUSIC_DISC_CHIRP": {
                return "C418 - chirp";
            }
            case "MUSIC_DISC_FAR": {
                return "C418 - far";
            }
            case "MUSIC_DISC_MALL": {
                return "C418 - mall";
            }
            case "MUSIC_DISC_STRAD": {
                return "C418 - strad";
            }
            case "MUSIC_DISC_WAIT": {
                return "C418 - wait";
            }
            case "MUSIC_DISC_CREATOR_MUSIC_BOX": {
                return "Lena Raine - Creator";
            }
            case "MUSIC_DISC_LAVA_CHICKEN": {
                return "Hyper Potions - Lava Chicken";
            }
            case "MUSIC_DISC_BOUNCE": {
                return "fingerspit - Bounce";
            }
            case "MUSIC_DISC_RELIC": {
                return "Aaron Cherof - Relic";
            }
            case "MUSIC_DISC_OTHERSIDE": {
                return "Lena Raine - otherside";
            }
        }
        String string3 = "MUSIC_DISC_";
        if (string2.regionMatches(true, 0, string3, 0, string3.length())) {
            string2 = string2.substring(string3.length());
        }
        string2 = string2.toLowerCase(Locale.ROOT).replace('_', ' ').trim();
        return string2.isBlank() ? "music" : string2;
    }

    private void stopAllAtmosphereMusic() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.stopAtmosphereMusic(player);
        }
        this.atmosphereMusicService.reset();
    }

    private void stopAtmosphereMusic(Player player) {
        if (player == null) {
            return;
        }
        this.atmosphereMusicService.removeActionBarSuppression(player.getUniqueId());
        this.stopAtmosphereMusicSounds(player);
        this.clearActionBar(player.getUniqueId(), BackendActionBarCoordinator.Owner.MUSIC);
    }

    private void stopAllAtmosphereMusicSounds(List<Player> list) {
        for (Player player : list) {
            this.stopAtmosphereMusicSounds(player);
        }
    }

    private void stopAtmosphereMusicSounds(Player player) {
        if (player == null) {
            return;
        }
        HashSet<String> hashSet = new HashSet<String>(this.atmosphereMusicAllSounds());
        String currentSound = this.atmosphereMusicService.currentSound();
        if (currentSound != null && !currentSound.isBlank()) {
            hashSet.add(currentSound);
        }
        if (hashSet.isEmpty()) {
            return;
        }
        try {
            for (String string : hashSet) {
                player.stopSound(string);
            }
        }
        catch (RuntimeException runtimeException) {
            // Older sound keys are best-effort; the next server tick will continue normally.
        }
    }

    private List<String> atmosphereMusicAllSounds() {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : List.of("warm", "playful", "special")) {
            for (String string2 : this.atmosphereMusicTracks(string)) {
                arrayList.add(this.normalizeAtmosphereMusicSound(string2));
            }
        }
        return arrayList;
    }

    private void showAtmosphere(Player player, String string, long l) {
        if (!this.canShowAtmosphere(player)) {
            return;
        }
        UUID uUID = player.getUniqueId();
        if (!this.atmosphereActionBarService.showReady(uUID, l, this.atmosphereCooldownSeconds())) {
            return;
        }
        String string2 = this.pickAtmosphereMessage(uUID, string);
        if (string2 == null || string2.isBlank()) {
            return;
        }
        this.atmosphereActionBarService.activate(uUID, string2, l, this.atmosphereDurationSeconds(), true);
        this.notifyActionBar(player, BackendActionBarCoordinator.Owner.ATMOSPHERE, Component.text((String)string2, (TextColor)NamedTextColor.GRAY), this.atmosphereDurationSeconds() * 1000L);
    }

    private void tickActivitySessions(long l) {
        if (!this.activityEnabled() || this.currentServer != ServerId.SURVIVAL || !this.activityTriggerEnabled("session-minutes")) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.canUseActivity(player)) {
                continue;
            }
            if (!this.activityMessageService.sessionReady(player.getUniqueId(), l, this.activityThreshold("session-minutes", 30))) {
                continue;
            }
            if (this.tryShowActivity(player, "session-minutes", l)) {
                this.activityMessageService.markSessionShown(player.getUniqueId(), l);
            }
        }
    }

    private void recordActivity(Player player, String string, int n, long l) {
        if (!this.activityEnabled() || this.currentServer != ServerId.SURVIVAL || player == null || n <= 0 || !this.activityTriggerEnabled(string)) {
            return;
        }
        if (!this.activityMessageService.recordProgress(player.getUniqueId(), string, n, this.activityThreshold(string, this.defaultActivityThreshold(string)))) {
            return;
        }
        if (this.tryShowActivity(player, string, l)) {
            this.activityMessageService.resetProgress(player.getUniqueId(), string);
        }
    }

    private void recordHudStat(String string, Player player, long l) {
        this.recordHudStat(string, player, l, null, 0L);
    }

    private void recordHudStat(String string, Player player, long l, String string3, long l2) {
        if (player == null || l <= 0L || this.hudData == null) {
            return;
        }
        synchronized (this.hudIoLock) {
            this.reloadHudDataFromDisk();
            this.hudDataService.recordStat(this.hudData, string, player, l, string3, l2);
            this.saveHudData();
        }
        BackendBoardDefinition boardDefinition = this.boardDefinition(string);
        if (boardDefinition != null && boardDefinition.enabledOn(this.currentServer.proxyName)) {
            this.updateMetricBoards();
        }
    }

    private void recordMadeRoomSandboxAction(Player player, long l) {
        BackendBoardDefinition boardDefinition = this.boardDefinition("made-room");
        if (player == null || l <= 0L || this.hudData == null || boardDefinition == null) {
            return;
        }
        synchronized (this.hudIoLock) {
            this.reloadHudDataFromDisk();
            this.hudDataService.recordSandboxAction(this.hudData, boardDefinition.dataKey(), player, l, boardDefinition.trackBlocksChanged() && this.boardConfig.trackBlocksChanged(boardDefinition.dataKey()));
            this.saveHudData();
        }
        if (boardDefinition.enabledOn(this.currentServer.proxyName)) {
            this.updateMetricBoards();
        }
    }

    private BackendBoardDefinition boardDefinition(String string) {
        for (BackendBoardDefinition boardDefinition : BOARD_DEFINITIONS) {
            if (boardDefinition.dataKey().equals(string)) {
                return boardDefinition;
            }
        }
        return null;
    }

    private void reloadHudDataFromDisk() {
        if (this.hudDataFile == null) {
            return;
        }
        synchronized (this.hudIoLock) {
            this.hudData = this.yamlStore.load(this.hudDataFile);
            this.ensureHudDataDefaults();
        }
    }

    private boolean tryShowActivity(Player player, String string, long l) {
        if (!this.canUseActivity(player)) {
            return false;
        }
        UUID uUID = player.getUniqueId();
        if (!this.activityMessageService.canShow(uUID, string, l, this.activityGlobalCooldownSeconds(), this.activityCooldownSeconds(string))) {
            return false;
        }
        if (this.atmosphereActionBarService.hasActiveMessage(uUID, l)) {
            return false;
        }
        String string2 = this.pickAtmosphereMessage(uUID, "activity." + string);
        if (string2 == null || string2.isBlank()) {
            return false;
        }
        this.atmosphereActionBarService.activate(uUID, string2, l, this.atmosphereDurationSeconds(), false);
        this.activityMessageService.markShown(uUID, string, l);
        this.notifyActionBar(player, BackendActionBarCoordinator.Owner.ATMOSPHERE, Component.text((String)string2, (TextColor)NamedTextColor.GRAY), this.atmosphereDurationSeconds() * 1000L);
        return true;
    }

    private boolean canUseActivity(Player player) {
        if (player == null || !player.isOnline() || this.currentServer != ServerId.SURVIVAL || !this.canShowAtmosphere(player)) {
            return false;
        }
        InventoryHolder inventoryHolder = player.getOpenInventory().getTopInventory().getHolder();
        return !(inventoryHolder instanceof CubeeHolder) && !(inventoryHolder instanceof LoginHolder);
    }

    private boolean activityEnabled() {
        return this.atmosphereConfig.activityEnabled();
    }

    private boolean activityTriggerEnabled(String string) {
        return this.atmosphereConfig.activityTriggerEnabled(string);
    }

    private int activityGlobalCooldownSeconds() {
        return this.atmosphereConfig.activityGlobalCooldownSeconds();
    }

    private int activityThreshold(String string, int n) {
        return this.atmosphereConfig.activityThreshold(string, n);
    }

    private int activityCooldownSeconds(String string) {
        return this.atmosphereConfig.activityCooldownSeconds(string);
    }

    private int defaultActivityThreshold(String string) {
        return this.activityMessageService.defaultThreshold(string);
    }

    private String pickAtmosphereMessage(UUID uUID, String string) {
        List<String> list = this.messages == null ? List.of() : this.messages.getStringList("atmosphere." + string);
        return this.atmosphereActionBarService.pickMessage(uUID, string, list);
    }

    private boolean canShowAtmosphere(Player player) {
        UUID uUID = player.getUniqueId();
        return player.isOnline() && this.currentServer == ServerId.SURVIVAL && this.atmosphereEnabled() && !this.isBedrockPlayer(player) && !this.isAuthLocked(player) && !this.travelStateService.contains(uUID) && !this.sandboxStatusService.contains(uUID) && !this.careWorldStatusService.contains(uUID) && !this.pendingActionBarStatuses.contains(uUID) && !this.chainTasks.containsKey(uUID);
    }

    private boolean atmosphereEnabled() {
        return this.atmosphereConfig.enabled();
    }

    private int atmosphereDurationSeconds() {
        return this.atmosphereConfig.actionBarDurationSeconds();
    }

    private int atmosphereRepeatSeconds() {
        return this.atmosphereConfig.actionBarRepeatSeconds();
    }

    private int atmosphereCooldownSeconds() {
        return this.atmosphereConfig.actionBarCooldownSeconds();
    }

    private boolean atmosphereMusicEnabled() {
        return this.atmosphereConfig.musicEnabled();
    }

    private int atmosphereMusicDelaySeconds() {
        return this.atmosphereConfig.musicDelaySeconds();
    }

    private int atmosphereMusicGapSeconds() {
        return this.atmosphereConfig.musicGapSeconds();
    }

    private int atmosphereMusicActionBarRefreshTicks() {
        return this.atmosphereConfig.musicActionBarRefreshTicks();
    }

    private int atmosphereMusicActionBarResumeDelayTicks() {
        return this.atmosphereConfig.musicActionBarResumeDelayTicks();
    }

    private int atmosphereMusicWeight(String string) {
        return this.atmosphereConfig.musicWeight(string);
    }

    private int atmosphereMusicTrackSeconds(String string) {
        return this.atmosphereConfig.musicTrackSeconds(string);
    }

    private String normalizeAtmosphereMusicSound(String string) {
        String string2 = String.valueOf(string).trim();
        if (string2.regionMatches(true, 0, "MUSIC_DISC_", 0, "MUSIC_DISC_".length())) {
            return "music_disc." + string2.substring("MUSIC_DISC_".length()).toLowerCase(Locale.ROOT);
        }
        if (string2.regionMatches(true, 0, "minecraft:", 0, "minecraft:".length())) {
            return string2.substring("minecraft:".length()).toLowerCase(Locale.ROOT);
        }
        return string2.toLowerCase(Locale.ROOT);
    }

    private boolean canPlayAtmosphereMusic(Player player) {
        return player.isOnline() && this.currentServer == ServerId.LOBBY && !this.isBedrockPlayer(player) && !this.isAuthLocked(player);
    }

    private TimePhase timePhase(World world) {
        long l = world.getTime();
        if (l < 6000L) {
            return TimePhase.MORNING;
        }
        if (l < 12000L) {
            return TimePhase.DAY;
        }
        if (l < 13800L) {
            return TimePhase.SUNSET;
        }
        return TimePhase.NIGHT;
    }

    private WeatherPhase weatherPhase(World world) {
        if (world.isThundering()) {
            return WeatherPhase.THUNDER;
        }
        return world.hasStorm() ? WeatherPhase.RAIN : WeatherPhase.CLEAR;
    }

    private void bedrockButton(Object object, List<Runnable> list, String string, String string2, Runnable runnable) {
        ((SimpleForm.Builder)object).button((String)(this.isBedrockStatusLore(string2) ? string + "\n" + string2 : string));
        list.add(runnable);
    }

    private void bedrockButton(Object object, List<Runnable> list, Ui.ButtonSpec buttonSpec, Runnable runnable) {
        this.bedrockButton(object, list, buttonSpec.title(), buttonSpec.lore(), runnable);
    }

    private void bedrockButton(Object object, List<Runnable> list, Ui.ButtonSpec buttonSpec, String string, Runnable runnable) {
        this.bedrockButton(object, list, buttonSpec.title(), string, runnable);
    }

    private boolean isBedrockStatusLore(String string) {
        return string != null && ("busy.".equals(string) || "unavailable.".equals(string) || "not ready yet.".equals(string) || "return home.".equals(string) || "open.".equals(string) || "closed.".equals(string) || "current.".equals(string) || "public.".equals(string) || "private.".equals(string) || "keep.".equals(string) || "once.".equals(string) || "hide.".equals(string) || "show.".equals(string) || "nobody here.".equals(string) || "no holders.".equals(string) || "wants to reset.".equals(string) || string.matches("[0-9]+") || string.matches("[0-9]+ waiting\\.") || string.matches("[0-9]+ holders?\\.") || string.contains("wants to reset.") || string.contains("available.") || string.contains("here."));
    }

    private void sendBedrockForm(Player player, Object object2, List<Runnable> list) {
        UUID uUID = player.getUniqueId();
        long l = this.bedrockFormTokenCounter.incrementAndGet();
        this.bedrockActionTokens.remove(uUID);
        this.bedrockFormTokens.put(uUID, l);
        Consumer<Object> consumer = object -> {
            try {
                int n = ((Number)object.getClass().getMethod("clickedButtonId", new Class[0]).invoke(object, new Object[0])).intValue();
                if (n < 0 || n >= list.size()) {
                    return;
                }
                Long l2 = this.bedrockFormTokens.get(uUID);
                if (l2 == null || l2 != l) {
                    return;
                }
                this.bedrockFormTokens.remove(uUID, l);
                this.bedrockActionTokens.put(uUID, l);
                Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                    if (!player.isOnline()) {
                        this.bedrockActionTokens.remove(uUID, l);
                        return;
                    }
                    Long actionToken = this.bedrockActionTokens.get(uUID);
                    if (actionToken == null || actionToken != l) {
                        return;
                    }
                    this.bedrockActionTokens.remove(uUID, l);
                    ((Runnable)list.get(n)).run();
                }, 2L);
            }
            catch (ReflectiveOperationException | RuntimeException exception) {
                this.bedrockFormTokens.remove(uUID, l);
                this.bedrockActionTokens.remove(uUID, l);
                this.openJavaFallback(player);
            }
        };
        ((SimpleForm.Builder)object2).validResultHandler((Consumer)consumer);
        try {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), ((SimpleForm.Builder)object2).build());
        }
        catch (RuntimeException runtimeException) {
            this.bedrockFormTokens.remove(uUID, l);
            this.bedrockActionTokens.remove(uUID, l);
            this.openJavaFallback(player);
        }
    }

    private void openJavaFallback(Player player) {
        if (!player.isOnline()) {
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            player.openInventory(this.createBedrockJavaFallbackHomeInventory(player));
            return;
        }
        player.openInventory(this.createJavaHomeInventory(player));
    }

    private Inventory createBedrockJavaFallbackHomeInventory(Player player) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.HOME), (int)27, this.homeTitleComponent());
        for (BackendBedrockFallbackService.FallbackButton button : this.bedrockFallbackService.homeButtons(
                this.currentServer == ServerId.LOBBY,
                this.peopleShortcutPublic(player),
                this.sandboxAvailable(player),
                this.isAdmin(player))) {
            this.setBedrockFallbackButton(inventory, button);
        }
        return inventory;
    }

    private void setBedrockFallbackButton(Inventory inventory, BackendBedrockFallbackService.FallbackButton button) {
        switch (button) {
            case LOOK -> this.setButton(inventory, Ui.Home.LOOK);
            case PEOPLE -> this.setButton(inventory, Ui.Home.PEOPLE);
            case PLACES -> this.setButton(inventory, Ui.Home.PLACES);
            case SANDBOX -> this.setButton(inventory, Ui.Home.SANDBOX);
            case CARE -> this.setButton(inventory, Ui.Home.CARE);
        }
    }

    private CubeeHolder currentHolder(Player player) {
        CubeeHolder cubeeHolder;
        InventoryHolder inventoryHolder = player.getOpenInventory().getTopInventory().getHolder();
        return inventoryHolder instanceof CubeeHolder ? (cubeeHolder = (CubeeHolder)inventoryHolder) : null;
    }

    private void openDrawing(Player player) {
        if (!this.sandboxAvailable(player)) {
            this.switchCubeeSurface(player, CubeeSurface.HOME);
            this.openCubee(player);
            return;
        }
        if (this.routeBusyCubeeState(player)) {
            return;
        }
        this.switchCubeeSurface(player, CubeeSurface.SANDBOX);
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockDrawing(player);
            return;
        }
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.DRAWING), (int)27, (Component)Component.text((String)"Sandbox", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.Sandbox.BACK);
        this.setButton(inventory, Ui.Sandbox.UNDO);
        this.setButton(inventory, Ui.Sandbox.REDO);
        this.setButton(inventory, Ui.Sandbox.SET);
        this.setButton(inventory, Ui.Sandbox.WALL);
        this.setButton(inventory, Ui.Sandbox.FLOOR);
        this.setButton(inventory, Ui.Sandbox.REPLACE);
        this.setButton(inventory, Ui.Sandbox.CLONE);
        this.setButton(inventory, Ui.Sandbox.CLEAR);
        this.setButton(inventory, Ui.Sandbox.CIRCLE);
        this.setButton(inventory, Ui.Sandbox.FLIP);
        this.setButton(inventory, Ui.Sandbox.ROTATE);
        player.openInventory(inventory);
    }

    private void openAdminPlayerSendPlaces(Player player, Player target, int pageIndex) {
        if (!this.requireAdmin(player) || !this.adminPeopleActionService.canTarget(player, target)) {
            this.openAdminPeople(player, pageIndex);
            return;
        }
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockAdminPlayerSendPlaces(player, target, pageIndex);
            return;
        }
        CubeeHolder holder = new CubeeHolder(CubeePage.ADMIN_PLAYER_SEND_PLACES);
        holder.subject = target.getUniqueId();
        holder.pageIndex = pageIndex;
        Inventory inventory = Bukkit.createInventory((InventoryHolder)holder, 27, this.subpageTitle("Send", "Places"));
        List<ServerId> places = this.goTargets();
        for (int i = 0; i < places.size(); i++) this.setPlaceButton(inventory, 12 + i, places.get(i));
        this.setGeneralNav(inventory, true, null);
        player.openInventory(inventory);
    }

    private void openCloneConfirm(Player player) {
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockCloneConfirm(player);
            return;
        }
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.CLONE_CONFIRM), (int)27, (Component)Component.text((String)"Place it here?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.Confirm.PLACE);
        this.setButton(inventory, Ui.Confirm.CLONE_PREVIEW);
        this.setButton(inventory, Ui.Confirm.CANCEL);
        player.openInventory(inventory);
    }

    private void openClearConfirm(Player player) {
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockClearConfirm(player);
            return;
        }
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.CLEAR_CONFIRM), (int)27, (Component)Component.text((String)"Clear this area?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.Confirm.CLEAR);
        this.setButton(inventory, Ui.Confirm.CLEAR_PREVIEW);
        this.setButton(inventory, Ui.Confirm.CANCEL);
        player.openInventory(inventory);
    }

    private void openRotateConfirm(Player player) {
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockRotateConfirm(player);
            return;
        }
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.ROTATE_CONFIRM), (int)27, (Component)Component.text((String)"Place it turned?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.Confirm.PLACE);
        this.setButton(inventory, Ui.Confirm.ROTATE_PREVIEW);
        this.setButton(inventory, Ui.Confirm.CANCEL);
        player.openInventory(inventory);
    }

    private void openFlipConfirm(Player player) {
        if (this.shouldOpenBedrockPage(player)) {
            this.openBedrockFlipConfirm(player);
            return;
        }
        Inventory inventory = Bukkit.createInventory((InventoryHolder)new CubeeHolder(CubeePage.FLIP_CONFIRM), (int)27, (Component)Component.text((String)"Place it mirrored?", (TextColor)HoneyPalette.DEFAULT_WHITE));
        this.setButton(inventory, Ui.Confirm.PLACE);
        this.setButton(inventory, Ui.Confirm.FLIP_PREVIEW);
        this.setButton(inventory, Ui.Confirm.CANCEL);
        player.openInventory(inventory);
    }

    private void startDrawing(Player player, DrawingAction drawingAction) {
        if (this.isBusy(player.getUniqueId())) {
            return;
        }
        this.clearDrawingPreviews(player.getUniqueId());
        DrawingState drawingState = new DrawingState(drawingAction);
        this.putDrawingState(player.getUniqueId(), drawingState);
        this.touchSandboxIdleTimeout(player);
        player.closeInventory();
        BackendSandboxDrawingTransitionService.StartPlan startPlan = this.sandboxDrawingTransitionService.startPlan(drawingAction.name());
        if (startPlan.startsWithSize()) {
            drawingState.inputStep = DrawingInputStep.SIZE;
            this.sendSandboxGuidance(player, "Size.", this.sandboxInputModelService.sizeGuidance());
            return;
        }
        String string = startPlan.moreTool() ? this.promptText("use-more-tool", "Use wooden hoe.") : this.promptText("use-basic-tool", "Use wooden axe.");
        this.sendSandboxGuidance(player, string, "First corner.");
    }

    private void handleDrawingInteract(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        if (this.handleClonePreviewRelocation(playerInteractEvent)) {
            return;
        }
        DrawingState drawingState = this.drawingState(player.getUniqueId());
        if (drawingState == null || playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getClickedBlock() == null) {
            return;
        }
        ItemStack itemStack = playerInteractEvent.getItem();
        Material material = this.isMoreDrawing(drawingState.action) ? this.moreToolMaterial() : this.basicToolMaterial();
        Material material2 = material;
        if (itemStack == null || itemStack.getType() != material) {
            return;
        }
        playerInteractEvent.setCancelled(true);
        this.touchSandboxIdleTimeout(player);
        Block block = playerInteractEvent.getClickedBlock();
        switch (this.sandboxDrawingTransitionService.blockClickRoute(drawingState.action.name(), drawingState.inputStep.name(), drawingState.first != null, drawingState.second != null, drawingState.blockData != null)) {
            case CIRCLE_BLOCK_PICK -> {
                drawingState.blockData = block.getBlockData();
                drawingState.inputStep = DrawingInputStep.READY;
                this.sendSandboxGuidance(player, this.promptText("use-more-tool", "Use wooden hoe."), "Center.");
            }
            case REJECT_BLOCK_CLICK -> this.failDrawingInput(player, drawingState, "try again.");
            case CIRCLE_CENTER_APPLY -> this.applySphere(player, drawingState, block.getLocation(), drawingState.blockData);
            case REPLACE_SOURCE_PICK -> {
                drawingState.sourceBlockData = block.getBlockData();
                drawingState.inputStep = DrawingInputStep.NEW_BLOCK;
                this.sendSandboxGuidance(player, "New block.", "Pick what it becomes.");
            }
            case NEW_BLOCK_PICK -> {
                drawingState.blockData = block.getBlockData();
                this.applyDrawing(player, drawingState, drawingState.blockData);
            }
            case FIRST_CORNER -> {
                drawingState.first = block.getLocation();
                this.playHomeSound(player, "sandbox-first");
                String toolPrompt = this.isMoreDrawing(drawingState.action)
                        ? this.promptText("use-more-tool", "Use wooden hoe.")
                        : this.promptText("use-basic-tool", "Use wooden axe.");
                this.sendSandboxGuidance(player, toolPrompt, "Second corner.");
            }
            case SECOND_CORNER -> {
                drawingState.second = block.getLocation();
                if (!this.validSelection(player, drawingState)) {
                    this.removeDrawingState(player.getUniqueId());
                    this.cancelSandboxIdleTimeout(player.getUniqueId());
                    this.sendSandboxStatus(player, "too large.", NamedTextColor.DARK_GRAY);
                    return;
                }
                this.playHomeSound(player, "sandbox-second");
                this.showSelection(player, drawingState);
                this.handleDrawingSelectionTransition(player, drawingState);
            }
            case CLONE_PLACE -> this.createClonePreview(player, drawingState, block.getLocation());
            case FINAL_BLOCK_APPLY -> {
                drawingState.blockData = block.getBlockData();
                this.applyDrawing(player, drawingState, drawingState.blockData);
            }
            case IGNORE -> {
            }
        }
    }

    private boolean handleClonePreviewRelocation(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK || playerInteractEvent.getClickedBlock() == null) {
            return false;
        }
        Player player = playerInteractEvent.getPlayer();
        Object preview = this.sandboxPreviewService.clonePreview(player.getUniqueId());
        if (!(preview instanceof ClonePreview currentPreview)) {
            return false;
        }
        ItemStack itemStack = playerInteractEvent.getItem();
        if (itemStack == null || itemStack.getType() != this.basicToolMaterial()) {
            return false;
        }
        playerInteractEvent.setCancelled(true);
        this.touchSandboxIdleTimeout(player);
        Location target = playerInteractEvent.getClickedBlock().getLocation();
        if (!currentPreview.world.equals(target.getWorld())) {
            this.sendSandboxCloneRelocationFailure(player);
            return true;
        }
        int minX = target.getBlockX();
        int minY = target.getBlockY();
        int minZ = target.getBlockZ();
        if (minX == currentPreview.minX && minY == currentPreview.minY && minZ == currentPreview.minZ) {
            this.sendSandboxCloneReadyStatus(player, currentPreview.movesLeft);
            return true;
        }
        if (currentPreview.movesLeft <= 0) {
            this.sendSandboxCloneReadyStatus(player, 0);
            return true;
        }
        int maxX = minX + (currentPreview.maxX - currentPreview.minX);
        int maxY = minY + (currentPreview.maxY - currentPreview.minY);
        int maxZ = minZ + (currentPreview.maxZ - currentPreview.minZ);
        if (minY < currentPreview.world.getMinHeight() || maxY >= currentPreview.world.getMaxHeight()) {
            this.sendSandboxCloneRelocationFailure(player);
            return true;
        }
        ClonePreview relocated = new ClonePreview(currentPreview.world, minX, minY, minZ, maxX, maxY, maxZ, currentPreview.movesLeft - 1);
        for (CloneBlock cloneBlock : currentPreview.blocks) {
            relocated.blocks.add(new CloneBlock(cloneBlock.offsetX, cloneBlock.offsetY, cloneBlock.offsetZ, cloneBlock.blockData));
        }
        relocated.entities.addAll(currentPreview.entities);
        this.sandboxPreviewService.setClone(player.getUniqueId(), relocated);
        this.showBox(player, relocated.world, relocated.minX, relocated.minY, relocated.minZ, relocated.maxX, relocated.maxY, relocated.maxZ);
        this.sendSandboxCloneReadyStatus(player, relocated.movesLeft);
        return true;
    }

    private void sendSandboxCloneRelocationFailure(Player player) {
        this.notifyActionBar(player, BackendActionBarCoordinator.Owner.SANDBOX_NOTIFICATION, Component.text("too large", NamedTextColor.DARK_GRAY), 3000);
    }

    @EventHandler
    public void onDrawingChat(AsyncChatEvent asyncChatEvent) {
        DrawingState drawingState = this.drawingState(asyncChatEvent.getPlayer().getUniqueId());
        if (drawingState == null || !this.expectsDrawingChat(drawingState)) {
            return;
        }
        asyncChatEvent.setCancelled(true);
        String string = PlainTextComponentSerializer.plainText().serialize(asyncChatEvent.message());
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.handleDrawingInput(asyncChatEvent.getPlayer(), drawingState, string.trim()));
    }

    private boolean validSelection(Player player, DrawingState drawingState) {
        return this.sandboxGeometryService.validSelection(drawingState.first, drawingState.second, this.sandboxMaxBlocks());
    }

    private boolean isMoreDrawing(DrawingAction drawingAction) {
        return this.sandboxDrawingTransitionService.usesMoreTool(drawingAction.name());
    }

    private boolean expectsDrawingChat(DrawingState drawingState) {
        return this.sandboxDrawingTransitionService.expectsChat(drawingState.inputStep.name(), drawingState.first != null && drawingState.second != null, drawingState.action.name());
    }

    private void handleDrawingSelectionTransition(Player player, DrawingState drawingState) {
        switch (this.sandboxDrawingTransitionService.afterSelection(drawingState.action.name())) {
            case CLEAR_PREVIEW -> this.createClearPreview(player, drawingState);
            case CLONE_PLACE -> this.sendSandboxGuidance(player, "Place.", "Choose where it lands.");
            case REPLACE_SOURCE -> {
                drawingState.inputStep = DrawingInputStep.REPLACE_SOURCE;
                this.sendSandboxGuidance(player, "Current block.", "Pick what to replace.");
            }
            case FLIP_INPUT -> {
                drawingState.inputStep = DrawingInputStep.FLIP;
                this.sendSandboxGuidance(player, "Flip.", "Type x or z.");
            }
            case ROTATION_INPUT -> {
                drawingState.inputStep = DrawingInputStep.ROTATION;
                this.sendSandboxGuidance(player, "Rotate.", "Type 90, 180, or 270.");
            }
            case BLOCK_INPUT -> this.sendSandboxGuidance(player, "Block.", "Choose what it becomes.");
        }
    }

    private void handleDrawingInput(Player player, DrawingState drawingState, String string) {
        if (!player.isOnline() || this.drawingState(player.getUniqueId()) != drawingState) {
            return;
        }
        this.touchSandboxIdleTimeout(player);
        switch (this.sandboxDrawingTransitionService.inputRoute(drawingState.inputStep.name())) {
            case SIZE -> this.handleSizeInput(player, drawingState, string);
            case CIRCLE_BLOCK -> this.handleCircleBlockInput(player, drawingState, string);
            case REPLACE_SOURCE -> this.handleOldBlockInput(player, drawingState, string);
            case NEW_BLOCK -> this.handleNewBlockInput(player, drawingState, string);
            case FLIP -> this.handleFlipInput(player, drawingState, string);
            case ROTATION -> this.handleRotationInput(player, drawingState, string);
            case BASIC_BLOCK -> this.handleBasicBlockInput(player, drawingState, string);
        }
    }

    private void handleSizeInput(Player player, DrawingState drawingState, String string) {
        int n = this.parseInt(string, -1);
        BackendSandboxInputModelService.Range range = this.sandboxInputModelService.sizeRange();
        if (!this.sandboxInputModelService.inRange(n, range)) {
            this.failDrawingInput(player, drawingState, this.sandboxInputModelService.failureMessage(n, range));
            return;
        }
        drawingState.size = n;
        drawingState.inputStep = DrawingInputStep.CIRCLE_BLOCK;
        this.sendSandboxGuidance(player, "Block.", "Choose what it becomes.");
    }

    private void handleNewBlockInput(Player player, DrawingState drawingState, String string) {
        Material material = this.sandboxMaterialInputService.newBlockMaterial(string, drawingState.action == DrawingAction.REPLACE, this.replaceTargetMaterial(), this.defaultSandboxMaterial());
        if (material == null) {
            this.failDrawingInput(player, drawingState, this.sandboxMaterialInputService.failureMessage());
            return;
        }
        drawingState.blockData = material.createBlockData();
        this.applyDrawing(player, drawingState, drawingState.blockData);
    }

    private void handleOldBlockInput(Player player, DrawingState drawingState, String string) {
        Material material = this.sandboxMaterialInputService.oldBlockMaterial(string, this.replaceSourceMaterial());
        if (material == null) {
            this.failDrawingInput(player, drawingState, this.sandboxMaterialInputService.failureMessage());
            return;
        }
        drawingState.sourceBlockData = material.createBlockData();
        drawingState.inputStep = DrawingInputStep.NEW_BLOCK;
        this.sendSandboxGuidance(player, "New block.", "Pick what it becomes.");
    }

    private void handleCircleBlockInput(Player player, DrawingState drawingState, String string) {
        Material material = this.sandboxMaterialInputService.circleBlockMaterial(string, this.defaultSandboxMaterial());
        if (material == null) {
            this.failDrawingInput(player, drawingState, this.sandboxMaterialInputService.failureMessage());
            return;
        }
        drawingState.blockData = material.createBlockData();
        drawingState.inputStep = DrawingInputStep.READY;
        this.sendSandboxGuidance(player, this.promptText("use-more-tool", "Use wooden hoe."), "Center.");
    }

    private void handleRotationInput(Player player, DrawingState drawingState, String string) {
        Integer rotation = this.sandboxTransformInputService.rotation(string);
        if (rotation == null) {
            this.failDrawingInput(player, drawingState, this.sandboxTransformInputService.failureMessage());
            return;
        }
        drawingState.rotation = rotation;
        this.createRotatePreview(player, drawingState);
    }

    private void handleFlipInput(Player player, DrawingState drawingState, String string) {
        Character flipAxis = this.sandboxTransformInputService.flipAxis(string);
        if (flipAxis == null) {
            this.failDrawingInput(player, drawingState, this.sandboxTransformInputService.failureMessage());
            return;
        }
        drawingState.flipAxis = flipAxis;
        this.createFlipPreview(player, drawingState);
    }

    private void handleBasicBlockInput(Player player, DrawingState drawingState, String string) {
        if (drawingState.action == DrawingAction.CIRCLE) {
            this.failDrawingInput(player, drawingState, this.sandboxMaterialInputService.failureMessage());
            return;
        }
        Material material = this.sandboxMaterialInputService.basicBlockMaterial(string);
        if (material == null) {
            this.failDrawingInput(player, drawingState, this.sandboxMaterialInputService.failureMessage());
            return;
        }
        drawingState.blockData = material.createBlockData();
        this.applyDrawing(player, drawingState, drawingState.blockData);
    }

    private void failDrawingInput(Player player, DrawingState drawingState, String string) {
        BackendSandboxInputFailureService.FailureResult failureResult = this.sandboxInputFailureService.fail(drawingState.failedInputs, string);
        drawingState.failedInputs = failureResult.failures();
        if (failureResult.closeSession()) {
            this.removeDrawingState(player.getUniqueId());
            this.cancelSandboxIdleTimeout(player.getUniqueId());
            this.sendSandboxStatus(player, failureResult.message(), failureResult.color());
            return;
        }
        this.sendSandboxStatus(player, failureResult.message(), failureResult.color());
    }

    private int parseInt(String string, int n) {
        try {
            return Integer.parseInt(string.trim());
        }
        catch (NumberFormatException numberFormatException) {
            return n;
        }
    }

    private int volume(DrawingState drawingState) {
        return this.sandboxGeometryService.volume(drawingState.first, drawingState.second);
    }

    private void applyDrawing(Player player, DrawingState drawingState, BlockData blockData) {
        if (!this.validSelection(player, drawingState)) {
            this.removeDrawingState(player.getUniqueId());
            this.cancelSandboxIdleTimeout(player.getUniqueId());
            this.sendSandboxStatus(player, "too large.", NamedTextColor.DARK_GRAY);
            return;
        }
        if (drawingState.action == DrawingAction.REPLACE && drawingState.sourceBlockData == null) {
            this.removeDrawingState(player.getUniqueId());
            this.cancelSandboxIdleTimeout(player.getUniqueId());
            this.sendSandboxStatus(player, "nothing changed.", NamedTextColor.DARK_GRAY);
            return;
        }
        BackendSandboxGeometryService.SelectionBounds bounds = this.sandboxGeometryService.bounds(drawingState.first, drawingState.second);
        World world = drawingState.first.getWorld();
        DrawingChange drawingChange = new DrawingChange();
        this.sandboxBulkDrawingService.buildChanges(
                world,
                new BackendSandboxBulkDrawingService.Bounds(bounds.minX(), bounds.maxX(), bounds.minY(), bounds.maxY(), bounds.minZ(), bounds.maxZ()),
                this.drawingShapePolicy(drawingState.action),
                drawingState.action == DrawingAction.REPLACE,
                drawingState.sourceBlockData,
                blockData,
                (changeWorld, x, y, z, oldData, newData) -> drawingChange.blocks.add(new BlockChange(changeWorld, x, y, z, oldData, newData)));
        this.removeDrawingState(player.getUniqueId());
        BackendSandboxBulkDrawingService.ApplyStatus applyStatus = this.sandboxBulkDrawingService.applyStatus(this.applyAndVerifyNewData(drawingChange), !drawingChange.blocks.isEmpty());
        if (!applyStatus.verified()) {
            this.cancelSandboxIdleTimeout(player.getUniqueId());
            this.sendSandboxStatus(player, applyStatus.message(), applyStatus.color());
            return;
        }
        this.recordChange(player, drawingChange);
        this.sendSandboxStatus(player, applyStatus.message(), applyStatus.color());
        this.cancelSandboxIdleTimeout(player.getUniqueId());
    }

    private boolean sameBlockData(BlockData blockData, BlockData blockData2) {
        return this.sandboxBlockChangeService.sameBlockData(blockData, blockData2);
    }

    private boolean sameExactBlockData(BlockData blockData, BlockData blockData2) {
        return this.sandboxBlockChangeService.sameExactBlockData(blockData, blockData2);
    }

    private void addBlockChange(DrawingChange drawingChange, World world, int n, int n2, int n3, BlockData blockData, BlockData blockData2) {
        if (!this.sandboxBlockChangeService.shouldAddChange(blockData, blockData2)) {
            return;
        }
        drawingChange.blocks.add(new BlockChange(world, n, n2, n3, blockData, blockData2));
    }

    private void applyNewData(DrawingChange drawingChange) {
        for (BlockChange blockChange : drawingChange.blocks) {
            blockChange.world.getBlockAt(blockChange.x, blockChange.y, blockChange.z).setBlockData(blockChange.newData, false);
        }
    }

    private boolean applyAndVerifyNewData(DrawingChange drawingChange) {
        this.applyNewData(drawingChange);
        if (!this.verifyChangeApplied(drawingChange, false)) {
            this.applyChange(drawingChange, true);
            return false;
        }
        if (this.spawnClonedEntities(drawingChange)) {
            return true;
        }
        this.applyChange(drawingChange, true);
        this.verifyChangeApplied(drawingChange, true);
        return false;
    }

    private boolean applyAndVerifyChange(DrawingChange drawingChange, boolean bl) {
        if (bl) {
            boolean entitiesRemoved = this.removeClonedEntities(drawingChange);
            this.applyChange(drawingChange, true);
            if (entitiesRemoved && this.verifyChangeApplied(drawingChange, true)) {
                return true;
            }
            this.applyChange(drawingChange, false);
            this.verifyChangeApplied(drawingChange, false);
            this.spawnClonedEntities(drawingChange);
            return false;
        }
        this.applyChange(drawingChange, false);
        if (!this.verifyChangeApplied(drawingChange, false)) {
            this.applyChange(drawingChange, true);
            return false;
        }
        if (this.spawnClonedEntities(drawingChange)) {
            return true;
        }
        this.applyChange(drawingChange, true);
        this.verifyChangeApplied(drawingChange, true);
        return false;
    }

    private boolean spawnClonedEntities(DrawingChange drawingChange) {
        if (drawingChange.entities.isEmpty()) {
            return true;
        }
        ArrayList<Entity> created = new ArrayList<Entity>();
        HashMap<UUID, Entity> createdBySource = new HashMap<UUID, Entity>();
        try {
            for (EntityChange entityChange : drawingChange.entities) {
                if (entityChange.spawnedId != null) {
                    Entity existing = entityChange.world.getEntity(entityChange.spawnedId);
                    if (existing != null && existing.isValid()) {
                        throw new IllegalStateException("Clone entity is already active.");
                    }
                    entityChange.spawnedId = null;
                }
                Entity entity = entityChange.snapshot.createEntity(entityChange.target.clone());
                if (entity == null || !entity.isValid() || entity.getUniqueId().equals(entityChange.sourceId)) {
                    throw new IllegalStateException("Clone entity creation failed.");
                }
                entityChange.spawnedId = entity.getUniqueId();
                created.add(entity);
                createdBySource.put(entityChange.sourceId, entity);
            }
            for (EntityChange entityChange : drawingChange.entities) {
                if (entityChange.vehicleSourceId == null) {
                    continue;
                }
                Entity passenger = createdBySource.get(entityChange.sourceId);
                Entity vehicle = createdBySource.get(entityChange.vehicleSourceId);
                if (passenger != null && vehicle != null && !vehicle.addPassenger(passenger)) {
                    throw new IllegalStateException("Clone passenger relationship could not be restored.");
                }
            }
            return true;
        } catch (RuntimeException exception) {
            for (Entity entity : created) {
                if (entity != null && entity.isValid()) {
                    entity.remove();
                }
            }
            for (EntityChange entityChange : drawingChange.entities) {
                entityChange.spawnedId = null;
            }
            this.getLogger().log(java.util.logging.Level.WARNING, "Sandbox Clone entity placement failed and was rolled back.", exception);
            return false;
        }
    }

    private boolean removeClonedEntities(DrawingChange drawingChange) {
        boolean removed = true;
        for (EntityChange entityChange : drawingChange.entities) {
            UUID spawnedId = entityChange.spawnedId;
            if (spawnedId == null) {
                continue;
            }
            Entity entity = entityChange.world.getEntity(spawnedId);
            if (entity != null && entity.isValid()) {
                entity.remove();
                if (entity.isValid()) {
                    removed = false;
                    continue;
                }
            }
            entityChange.spawnedId = null;
        }
        return removed;
    }

    private boolean verifyChangeApplied(DrawingChange drawingChange, boolean bl) {
        for (BlockChange blockChange : drawingChange.blocks) {
            BlockData blockData = this.sandboxBlockChangeService.targetData(blockChange.oldData, blockChange.newData, bl);
            BlockData blockData2 = blockData;
            BlockData blockData3 = blockChange.world.getBlockAt(blockChange.x, blockChange.y, blockChange.z).getBlockData();
            if (this.sameExactBlockData(blockData3, blockData)) continue;
            return false;
        }
        return true;
    }

    private BackendSandboxDrawingShapeService.ShapePolicy drawingShapePolicy(DrawingAction drawingAction) {
        return switch (drawingAction) {
            case SET, CLEAR, REPLACE -> BackendSandboxDrawingShapeService.ShapePolicy.VOLUME;
            case WALL -> BackendSandboxDrawingShapeService.ShapePolicy.WALL;
            case FLOOR -> BackendSandboxDrawingShapeService.ShapePolicy.FLOOR;
            case CLONE, CIRCLE, FLIP, ROTATE -> BackendSandboxDrawingShapeService.ShapePolicy.NONE;
        };
    }

    private void applySphere(Player player, DrawingState drawingState, Location location, BlockData blockData) {
        int n = drawingState.size;
        if (this.sandboxCenterDrawingService.sphereTooLarge(n, this.sandboxMaxBlocks())) {
            this.finishActiveDrawing(player.getUniqueId());
            this.sendSandboxStatus(player, "too large.", NamedTextColor.DARK_GRAY);
            return;
        }
        World world = player.getWorld();
        int n3 = location.getBlockX();
        int n4 = location.getBlockY();
        int n5 = location.getBlockZ();
        if (!this.sandboxCenterDrawingService.withinVerticalRange(n4, n, world.getMinHeight(), world.getMaxHeight())) {
            this.finishActiveDrawing(player.getUniqueId());
            this.sendSandboxStatus(player, "too large.", NamedTextColor.DARK_GRAY);
            return;
        }
        DrawingChange drawingChange = new DrawingChange();
        this.sandboxCenterDrawingService.forEachSphereBlock(n3, n4, n5, n, (x, y, z) -> {
            Block block = world.getBlockAt(x, y, z);
            this.addBlockChange(drawingChange, world, x, y, z, block.getBlockData(), blockData);
        });
        if (!this.applyAndVerifyNewData(drawingChange)) {
            this.finishActiveDrawing(player.getUniqueId());
            this.sendSandboxStatus(player, "try again.", NamedTextColor.DARK_GRAY);
            return;
        }
        this.recordChange(player, drawingChange);
        this.sendRepeatDrawingStatus(player, drawingState, drawingChange);
    }

    private int sphereAffectedBlocks(int n) {
        return this.sandboxCenterDrawingService.sphereAffectedBlocks(n);
    }

    private void sendRepeatDrawingStatus(Player player, DrawingState drawingState, DrawingChange drawingChange) {
        BackendSandboxCenterDrawingService.RepeatStatus repeatStatus = this.sandboxCenterDrawingService.repeatStatus(!drawingChange.blocks.isEmpty(), drawingState.repeatDoneShown);
        drawingState.repeatDoneShown = repeatStatus.repeatDoneShown();
        if (repeatStatus.sendMainStatus()) {
            this.sendSandboxStatus(player, repeatStatus.message(), repeatStatus.color());
        }
        this.setSandboxStatus(player, (Component)Component.text((String)"repeat", (TextColor)NamedTextColor.GRAY));
    }

    private void createRotatePreview(Player player, DrawingState drawingState) {
        this.createTransformPreview(player, drawingState, true);
    }

    private void createFlipPreview(Player player, DrawingState drawingState) {
        this.createTransformPreview(player, drawingState, false);
    }

    private void createTransformPreview(Player player, DrawingState drawingState, boolean rotate) {
        BackendSandboxTransformPreviewService.PreviewStatus previewStatus = this.sandboxTransformPreviewService.previewStatus(!this.validSelection(player, drawingState), false, false, false);
        if (this.finishFailedTransformPreview(player, previewStatus)) {
            return;
        }
        BackendSandboxGeometryService.SelectionBounds bounds = this.sandboxGeometryService.bounds(drawingState.first, drawingState.second);
        World world = drawingState.first.getWorld();
        double d = (double)(bounds.minX() + bounds.maxX()) / 2.0;
        double d2 = (double)(bounds.minZ() + bounds.maxZ()) / 2.0;
        BackendSandboxTransformPreviewService.TransformPlan<BlockPosition> transformPlan = this.sandboxTransformPreviewService.build(bounds, (x, y, z) -> new BlockPosition(x, y, z), (x, y, z) -> {
            if (rotate) {
                BackendSandboxGeometryService.BlockPoint rotatedPoint = this.sandboxGeometryService.rotatedPosition(x, y, z, d, d2, drawingState.rotation);
                BlockPosition blockPosition = new BlockPosition(rotatedPoint.x(), rotatedPoint.y(), rotatedPoint.z());
                return new BackendSandboxTransformPreviewService.TransformedBlock<BlockPosition>(blockPosition, this.sandboxGeometryService.rotateBlockData(world.getBlockAt(x, y, z).getBlockData(), drawingState.rotation));
            }
            BackendSandboxGeometryService.BlockPoint flippedPoint = this.sandboxGeometryService.flippedPosition(x, y, z, d, d2, drawingState.flipAxis);
            BlockPosition blockPosition = new BlockPosition(flippedPoint.x(), flippedPoint.y(), flippedPoint.z());
                return new BackendSandboxTransformPreviewService.TransformedBlock<BlockPosition>(blockPosition, this.sandboxGeometryService.flipBlockData(world.getBlockAt(x, y, z).getBlockData(), drawingState.flipAxis));
        });
        previewStatus = this.sandboxTransformPreviewService.previewStatus(false, this.sandboxTransformPreviewService.tooManyPositions(transformPlan, this.sandboxMaxBlocks()), false, false);
        if (this.finishFailedTransformPreview(player, previewStatus)) {
            return;
        }
        previewStatus = this.sandboxTransformPreviewService.previewStatus(false, false, !this.sandboxTransformPreviewService.allWithinVerticalRange(transformPlan.affectedPositions(), blockPosition -> blockPosition.y, world.getMinHeight(), world.getMaxHeight()), false);
        if (this.finishFailedTransformPreview(player, previewStatus)) {
            return;
        }
        DrawingChange drawingChange = new DrawingChange();
        BlockData blockData = Material.AIR.createBlockData();
        BackendSandboxTransformPreviewService.TransformSummary<BlockPosition> transformSummary = this.sandboxTransformPreviewService.summarizeChanges(
                transformPlan.affectedPositions(),
                blockPosition -> world.getBlockAt(blockPosition.x, blockPosition.y, blockPosition.z).getBlockData(),
                blockPosition -> transformPlan.blockDataByPosition().getOrDefault(blockPosition, blockData),
                blockPosition -> blockPosition.x,
                blockPosition -> blockPosition.y,
                blockPosition -> blockPosition.z,
                this::sameExactBlockData);
        for (BackendSandboxTransformPreviewService.ChangedBlock<BlockPosition> changedBlock : transformSummary.changes()) {
            BlockPosition blockPosition = changedBlock.position();
            Block block = world.getBlockAt(blockPosition.x, blockPosition.y, blockPosition.z);
            this.addBlockChange(drawingChange, world, blockPosition.x, blockPosition.y, blockPosition.z, changedBlock.oldData(), changedBlock.newData());
        }
        previewStatus = this.sandboxTransformPreviewService.previewStatus(false, false, false, transformSummary.empty());
        if (this.finishFailedTransformPreview(player, previewStatus)) {
            return;
        }
        RotatePreview rotatePreview = new RotatePreview(world, transformSummary.minX(), transformSummary.minY(), transformSummary.minZ(), transformSummary.maxX(), transformSummary.maxY(), transformSummary.maxZ(), drawingChange);
        this.finishPreviewCreation(player, this.sandboxPreviewLifecycleService.transformKind(rotate), rotatePreview, world, transformSummary.minX(), transformSummary.minY(), transformSummary.minZ(), transformSummary.maxX(), transformSummary.maxY(), transformSummary.maxZ());
    }

    private boolean finishFailedTransformPreview(Player player, BackendSandboxTransformPreviewService.PreviewStatus previewStatus) {
        if (previewStatus.ready()) {
            return false;
        }
        BackendSandboxTransformPreviewService.PreviewFailureLifecycle lifecycle = this.sandboxTransformPreviewService.previewFailureLifecycle(previewStatus);
        if (lifecycle.removeDrawingState()) {
            this.removeDrawingState(player.getUniqueId());
        }
        if (lifecycle.sendStatus()) {
            this.sendSandboxStatus(player, previewStatus.message(), previewStatus.color());
        }
        return true;
    }

    private void setPreview(UUID uUID, BackendSandboxPreviewLifecycleService.PreviewKind previewKind, Object preview) {
        switch (previewKind) {
            case CLONE -> this.sandboxPreviewService.setClone(uUID, preview);
            case CLEAR -> this.sandboxPreviewService.setClear(uUID, preview);
            case ROTATE -> this.sandboxPreviewService.setRotate(uUID, preview);
            case FLIP -> this.sandboxPreviewService.setFlip(uUID, preview);
        }
    }

    private void finishPreviewCreation(Player player, BackendSandboxPreviewLifecycleService.PreviewKind previewKind, Object preview, World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        UUID uUID = player.getUniqueId();
        BackendSandboxPreviewLifecycleService.ReadyPreviewLifecycle lifecycle = this.sandboxPreviewLifecycleService.readyPreviewLifecycle();
        this.setPreview(uUID, previewKind, preview);
        if (lifecycle.removeDrawingState()) {
            this.removeDrawingState(uUID);
        }
        if (lifecycle.showBox()) {
            this.showBox(player, world, minX, minY, minZ, maxX, maxY, maxZ);
        }
        if (lifecycle.sendReadyStatus()) {
            if (previewKind == BackendSandboxPreviewLifecycleService.PreviewKind.CLONE) {
                this.sendSandboxCloneReadyStatus(player, ((ClonePreview)preview).movesLeft);
            } else {
                this.sendSandboxReadyStatus(player);
            }
        }
    }

    private void createClonePreview(Player player, DrawingState drawingState, Location location) {
        boolean validSelection = this.validSelection(player, drawingState);
        boolean differentWorld = validSelection && !drawingState.first.getWorld().equals((Object)location.getWorld());
        BackendSandboxCloneClearPreviewStatusService.PreviewStatus previewStatus = this.sandboxCloneClearPreviewStatusService.cloneStatus(!validSelection, differentWorld, false);
        if (!previewStatus.ready()) {
            this.removeDrawingState(player.getUniqueId());
            this.sendSandboxStatus(player, previewStatus.message(), previewStatus.color());
            return;
        }
        BackendSandboxGeometryService.SelectionBounds bounds = this.sandboxGeometryService.bounds(drawingState.first, drawingState.second);
        World world = drawingState.first.getWorld();
        BackendSandboxClonePreviewPlanService.ClonePreviewPlan clonePlan;
        try {
            clonePlan = this.sandboxClonePreviewPlanService.build(world, bounds, location, this::isSandboxCloneExcludedEntity);
        } catch (RuntimeException exception) {
            this.removeDrawingState(player.getUniqueId());
            this.getLogger().log(java.util.logging.Level.WARNING, "Sandbox Clone could not snapshot every selected entity.", exception);
            this.sendSandboxStatus(player, "try again.", NamedTextColor.DARK_GRAY);
            return;
        }
        previewStatus = this.sandboxCloneClearPreviewStatusService.cloneStatus(false, false, this.sandboxClonePreviewPlanService.outsideVerticalRange(clonePlan, world.getMinHeight(), world.getMaxHeight()));
        if (!previewStatus.ready()) {
            this.removeDrawingState(player.getUniqueId());
            this.sendSandboxStatus(player, previewStatus.message(), previewStatus.color());
            return;
        }
        ClonePreview clonePreview = new ClonePreview(world, clonePlan.minX(), clonePlan.minY(), clonePlan.minZ(), clonePlan.maxX(), clonePlan.maxY(), clonePlan.maxZ(), CLONE_RELOCATIONS);
        for (BackendSandboxClonePreviewPlanService.SourceBlock sourceBlock : clonePlan.blocks()) {
            clonePreview.blocks.add(new CloneBlock(sourceBlock.offsetX(), sourceBlock.offsetY(), sourceBlock.offsetZ(), sourceBlock.blockData()));
        }
        for (BackendSandboxClonePreviewPlanService.SourceEntity sourceEntity : clonePlan.entities()) {
            clonePreview.entities.add(new CloneEntity(
                    sourceEntity.sourceId(),
                    sourceEntity.vehicleSourceId(),
                    sourceEntity.offsetX(),
                    sourceEntity.offsetY(),
                    sourceEntity.offsetZ(),
                    sourceEntity.yaw(),
                    sourceEntity.pitch(),
                    sourceEntity.snapshot()));
        }
        this.finishPreviewCreation(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLONE, clonePreview, world, clonePreview.minX, clonePreview.minY, clonePreview.minZ, clonePreview.maxX, clonePreview.maxY, clonePreview.maxZ);
    }

    private void createClearPreview(Player player, DrawingState drawingState) {
        BackendSandboxGeometryService.SelectionBounds bounds = this.sandboxGeometryService.bounds(drawingState.first, drawingState.second);
        BackendSandboxClearPreviewPlanService.ClearPreviewPlan clearPlan = this.sandboxClearPreviewPlanService.build(bounds);
        BackendSandboxCloneClearPreviewStatusService.PreviewStatus previewStatus = this.sandboxCloneClearPreviewStatusService.clearStatus();
        if (!previewStatus.ready()) {
            this.removeDrawingState(player.getUniqueId());
            this.sendSandboxStatus(player, previewStatus.message(), previewStatus.color());
            return;
        }
        ClearPreview clearPreview = new ClearPreview(drawingState.first.getWorld(), clearPlan.minX(), clearPlan.minY(), clearPlan.minZ(), clearPlan.maxX(), clearPlan.maxY(), clearPlan.maxZ());
        this.finishPreviewCreation(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLEAR, clearPreview, drawingState.first.getWorld(), clearPlan.minX(), clearPlan.minY(), clearPlan.minZ(), clearPlan.maxX(), clearPlan.maxY(), clearPlan.maxZ());
    }

    private void placeClone(Player player) {
        BackendSandboxPlacementService.PlacementPreview placementPreview = this.removePlacementPreview(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLONE);
        if (placementPreview.missing()) {
            this.finishMissingSandboxPlacement(player);
            return;
        }
        ClonePreview clonePreview = (ClonePreview)placementPreview.preview();
        this.finishSandboxPlacement(player, this.buildClonePlacementChange(clonePreview));
    }

    private void placeClear(Player player) {
        BackendSandboxPlacementService.PlacementPreview placementPreview = this.removePlacementPreview(player, BackendSandboxPreviewLifecycleService.PreviewKind.CLEAR);
        if (placementPreview.missing()) {
            this.finishMissingSandboxPlacement(player);
            return;
        }
        ClearPreview clearPreview = (ClearPreview)placementPreview.preview();
        this.finishSandboxPlacement(player, this.buildClearPlacementChange(clearPreview));
    }

    private DrawingChange buildClonePlacementChange(ClonePreview clonePreview) {
        DrawingChange drawingChange = new DrawingChange();
        this.sandboxCloneClearChangeService.buildCloneChanges(clonePreview.world, this.clonePlacementBlocks(clonePreview), (world, x, y, z, oldData, newData) -> this.addBlockChange(drawingChange, world, x, y, z, oldData, newData));
        for (CloneEntity cloneEntity : clonePreview.entities) {
            Location target = new Location(
                    clonePreview.world,
                    clonePreview.minX + cloneEntity.offsetX,
                    clonePreview.minY + cloneEntity.offsetY,
                    clonePreview.minZ + cloneEntity.offsetZ,
                    cloneEntity.yaw,
                    cloneEntity.pitch);
            drawingChange.entities.add(new EntityChange(clonePreview.world, cloneEntity.sourceId, cloneEntity.vehicleSourceId, target, cloneEntity.snapshot));
        }
        return drawingChange;
    }

    private DrawingChange buildClearPlacementChange(ClearPreview clearPreview) {
        DrawingChange drawingChange = new DrawingChange();
        BlockData blockData = Material.AIR.createBlockData();
        this.sandboxCloneClearChangeService.buildClearChanges(clearPreview.world, clearPreview.minX, clearPreview.minY, clearPreview.minZ, clearPreview.maxX, clearPreview.maxY, clearPreview.maxZ, blockData, (world, x, y, z, oldData, newData) -> this.addBlockChange(drawingChange, world, x, y, z, oldData, newData));
        return drawingChange;
    }

    private List<BackendSandboxCloneClearChangeService.ClonePlacementBlock> clonePlacementBlocks(ClonePreview clonePreview) {
        ArrayList<BackendSandboxCloneClearChangeService.ClonePlacementBlock> blocks = new ArrayList<BackendSandboxCloneClearChangeService.ClonePlacementBlock>();
        for (CloneBlock cloneBlock : clonePreview.blocks) {
            blocks.add(new BackendSandboxCloneClearChangeService.ClonePlacementBlock(clonePreview.minX, clonePreview.minY, clonePreview.minZ, cloneBlock.offsetX, cloneBlock.offsetY, cloneBlock.offsetZ, cloneBlock.blockData));
        }
        return blocks;
    }

    private void placeRotate(Player player) {
        BackendSandboxPlacementService.PlacementPreview placementPreview = this.removePlacementPreview(player, BackendSandboxPreviewLifecycleService.PreviewKind.ROTATE);
        if (placementPreview.missing()) {
            this.finishMissingSandboxPlacement(player);
            return;
        }
        RotatePreview rotatePreview = (RotatePreview)placementPreview.preview();
        this.finishSandboxPlacement(player, rotatePreview.change);
    }

    private void placeFlip(Player player) {
        BackendSandboxPlacementService.PlacementPreview placementPreview = this.removePlacementPreview(player, BackendSandboxPreviewLifecycleService.PreviewKind.FLIP);
        if (placementPreview.missing()) {
            this.finishMissingSandboxPlacement(player);
            return;
        }
        RotatePreview rotatePreview = (RotatePreview)placementPreview.preview();
        this.finishSandboxPlacement(player, rotatePreview.change);
    }

    private BackendSandboxPlacementService.PlacementPreview removePlacementPreview(Player player, BackendSandboxPreviewLifecycleService.PreviewKind previewKind) {
        Object preview = this.removePreview(player.getUniqueId(), previewKind);
        this.cancelSandboxIdleTimeout(player.getUniqueId());
        return this.sandboxPlacementService.preview(preview);
    }

    private void finishMissingSandboxPlacement(Player player) {
        BackendSandboxPlacementService.MissingPlacementLifecycle lifecycle = this.sandboxPlacementService.missingLifecycle();
        if (lifecycle.closeInventory()) {
            player.closeInventory();
        }
        BackendSandboxPlacementService.PlacementStatus placementStatus = this.sandboxPlacementService.missingPreview();
        if (lifecycle.sendStatus()) {
            this.sendSandboxStatus(player, placementStatus.message(), placementStatus.color());
        }
    }

    private void finishSandboxPlacement(Player player, DrawingChange drawingChange) {
        boolean applied = this.applyAndVerifyNewData(drawingChange);
        BackendSandboxPlacementService.PlacementResult placementResult = this.sandboxPlacementService.result(applied, drawingChange.isEmpty());
        BackendSandboxPlacementService.PlacementCompletionLifecycle lifecycle = this.sandboxPlacementService.completionLifecycle(placementResult);
        if (lifecycle.recordChange()) {
            this.recordChange(player, drawingChange);
        }
        if (lifecycle.closeInventory()) {
            player.closeInventory();
        }
        BackendSandboxPlacementService.PlacementStatus placementStatus = placementResult.status();
        if (lifecycle.sendStatus()) {
            this.sendSandboxStatus(player, placementStatus.message(), placementStatus.color());
        }
    }

    private void undoDrawingIfIdle(Player player) {
        this.runUndoRedoIfIdle(player, true);
    }

    private void redoDrawingIfIdle(Player player) {
        this.runUndoRedoIfIdle(player, false);
    }

    private void runUndoRedoIfIdle(Player player, boolean undo) {
        switch (this.sandboxUndoRedoService.idleRoute(this.isBusy(player.getUniqueId()), undo)) {
            case BLOCKED -> this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.missingChange());
            case UNDO -> this.undoDrawing(player);
            case REDO -> this.redoDrawing(player);
        }
    }

    private void undoDrawing(Player player) {
        DrawingChange drawingChange = (DrawingChange)this.sandboxHistoryService.popUndo(player.getUniqueId());
        if (drawingChange == null) {
            this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.missingChange());
            return;
        }
        if (!this.applyAndVerifyChange(drawingChange, this.sandboxUndoRedoService.undoUsesOldData())) {
            this.sandboxHistoryService.restoreUndo(player.getUniqueId(), drawingChange);
            this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.applyFailed());
            return;
        }
        this.sandboxHistoryService.moveUndoToRedo(player.getUniqueId(), drawingChange);
        this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.applied());
    }

    private void redoDrawing(Player player) {
        DrawingChange drawingChange = (DrawingChange)this.sandboxHistoryService.popRedo(player.getUniqueId());
        if (drawingChange == null) {
            this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.missingChange());
            return;
        }
        if (!this.applyAndVerifyChange(drawingChange, this.sandboxUndoRedoService.redoUsesOldData())) {
            this.sandboxHistoryService.restoreRedo(player.getUniqueId(), drawingChange);
            this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.applyFailed());
            return;
        }
        this.sandboxHistoryService.moveRedoToUndo(player.getUniqueId(), drawingChange);
        this.sendSandboxUndoRedoStatus(player, this.sandboxUndoRedoService.applied());
    }

    private void sendSandboxUndoRedoStatus(Player player, BackendSandboxUndoRedoService.UndoRedoStatus undoRedoStatus) {
        this.sendSandboxStatus(player, undoRedoStatus.message(), undoRedoStatus.color());
    }

    private void recordChange(Player player, DrawingChange drawingChange) {
        this.sandboxHistoryService.clearRedo(player.getUniqueId());
        if (drawingChange.isEmpty()) {
            return;
        }
        if (this.currentServer == ServerId.CREATIVE) {
            this.recordMadeRoomSandboxAction(player, drawingChange.blocks.size());
        }
        this.sandboxHistoryService.record(player.getUniqueId(), drawingChange, this.sandboxHistoryLimit());
    }

    private void applyChange(DrawingChange drawingChange, boolean bl) {
        for (BlockChange blockChange : drawingChange.blocks) {
            blockChange.world.getBlockAt(blockChange.x, blockChange.y, blockChange.z).setBlockData(this.sandboxBlockChangeService.targetData(blockChange.oldData, blockChange.newData, bl), false);
        }
    }

    private void showSelection(Player player, DrawingState drawingState) {
        BackendSandboxGeometryService.SelectionBounds bounds = this.sandboxGeometryService.bounds(drawingState.first, drawingState.second);
        this.showBox(player, drawingState.first.getWorld(), bounds.minX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ());
    }

    private void showBox(Player player, World world, int n, int n2, int n3, int n4, int n5, int n6) {
        for (BackendSandboxPreviewRenderService.Line line : this.sandboxPreviewRenderService.boxLines(n, n2, n3, n4, n5, n6)) {
            this.drawPreviewLine(player, world, line);
        }
    }

    private void drawPreviewLine(Player player, World world, BackendSandboxPreviewRenderService.Line line) {
        int n = this.sandboxPreviewRenderService.lineSteps(line);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.WHITE, 1.0f);
        for (int i = 0; i <= n; ++i) {
            BackendSandboxPreviewRenderService.Point point = this.sandboxPreviewRenderService.pointAt(line, i, n);
            player.spawnParticle(Particle.DUST, point.x(), point.y(), point.z(), 1, 0.0, 0.0, 0.0, 0.0, (Object)dustOptions);
        }
    }

    private void ensureCubee(Player player) {
        this.ensureCubee(player, false);
    }

    private void ensureCubee(Player player, boolean bl) {
        this.applyCubeeInventoryMode(player, this.cubeeItemService.ensureMode(bl));
    }

    private void normalizeCubee(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        this.applyCubeeInventoryMode(player, this.cubeeItemService.normalizeMode());
    }

    private void applyCubeeInventoryMode(Player player, BackendCubeeItemService.InventoryMode mode) {
        boolean authLocked = this.isAuthLocked(player);
        boolean cubeeEnabled = !authLocked && this.cubeeEnabled();
        boolean cubeeVisible = cubeeEnabled && this.cubeeVisible(player);
        switch (this.cubeeItemService.accessAction(authLocked, cubeeEnabled, cubeeVisible)) {
            case HIDE_AUTH -> {
                this.hideAuthItems(player);
                this.finishCubeeInventoryMode(player, mode);
                return;
            }
            case PURGE -> {
                this.purgeCubeeItems(player);
                this.finishCubeeInventoryMode(player, mode);
                return;
            }
            case CONTINUE -> {
            }
        }
        int n = this.cubeeSlot();
        PlayerInventory playerInventory = player.getInventory();
        if (mode.fastKeep() && this.isCubee(playerInventory.getItem(n)) && !this.hasCubeeOutsideSystemSlot(playerInventory)) {
            this.selectCubeeSystemSlot(playerInventory, n, mode);
            return;
        }
        if (mode.purgeAll()) {
            this.cubeeItemService.purge(playerInventory);
        } else {
            this.cubeeItemService.purgeOutsideSystemSlot(playerInventory, n);
        }
        boolean occupiedByOtherItem = playerInventory.getItem(n) != null && !this.isCubee(playerInventory.getItem(n));
        boolean moved = occupiedByOtherItem && this.currentServer == ServerId.LOBBY && this.moveItemFromCubeeSlot(playerInventory);
        if (this.cubeeItemService.placementAction(occupiedByOtherItem, this.currentServer == ServerId.LOBBY, moved) == BackendCubeeItemService.PlacementAction.HIDE) {
            this.finishCubeeInventoryMode(player, mode);
            return;
        }
        playerInventory.setItem(n, this.cubeeItem());
        this.selectCubeeSystemSlot(playerInventory, n, mode);
        this.finishCubeeInventoryMode(player, mode);
    }

    private void selectCubeeSystemSlot(PlayerInventory playerInventory, int systemSlot, BackendCubeeItemService.InventoryMode mode) {
        if (mode.selectSystemSlot() && playerInventory.getHeldItemSlot() != systemSlot) {
            playerInventory.setHeldItemSlot(systemSlot);
        }
    }

    private void finishCubeeInventoryMode(Player player, BackendCubeeItemService.InventoryMode mode) {
        if (mode.updateInventory()) {
            player.updateInventory();
        }
    }

    private void repairCreativeCubeeSlot(Player player) {
        if (player == null || !player.isOnline() || this.currentServer != ServerId.CREATIVE || this.isAuthLocked(player)) {
            return;
        }
        if (!this.cubeeEnabled() || !this.cubeeVisible(player)) {
            return;
        }
        PlayerInventory playerInventory = player.getInventory();
        int systemSlot = this.cubeeSlot();
        this.cubeeItemService.purgeOutsideSystemSlot(playerInventory, systemSlot);
        if (!this.isCubee(playerInventory.getItem(systemSlot))) {
            playerInventory.setItem(systemSlot, this.cubeeItem());
            player.updateInventory();
        }
    }

    private boolean hasCubeeOutsideSystemSlot(PlayerInventory playerInventory) {
        return this.cubeeItemService.hasOutsideSystemSlot(playerInventory, this.cubeeSlot());
    }

    private boolean moveItemFromCubeeSlot(PlayerInventory playerInventory) {
        return this.cubeeItemService.moveItemFromSystemSlot(playerInventory, this.cubeeSlot());
    }

    private void purgeCubeeItems(Player player) {
        this.cubeeItemService.purge(player.getInventory());
    }

    private void hideAuthItems(Player player) {
        this.saveAuthInventory(player);
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();
        playerInventory.setItemInOffHand(null);
        playerInventory.setArmorContents(new ItemStack[4]);
        if (!this.isBedrockPlayer(player)) {
            int n = this.cubeeSlot();
            playerInventory.setItem(n, this.loginItem());
            playerInventory.setHeldItemSlot(n);
        }
    }

    private void saveAuthInventory(Player player) {
        if (this.currentServer == ServerId.LOBBY || this.authInventories.containsKey(player.getUniqueId())) {
            return;
        }
        this.authInventories.put(player.getUniqueId(), new SavedInventory(player.getInventory()));
    }

    private void restoreAuthInventory(Player player) {
        SavedInventory savedInventory = this.authInventories.remove(player.getUniqueId());
        if (savedInventory == null) {
            return;
        }
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.setContents(SavedInventory.cloneItems(savedInventory.contents));
        playerInventory.setArmorContents(SavedInventory.cloneItems(savedInventory.armor));
        playerInventory.setItemInOffHand(SavedInventory.cloneItem(savedInventory.offhand));
        playerInventory.setHeldItemSlot(savedInventory.heldSlot);
    }

    private void purgeLoginItems(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        for (int i = 0; i < playerInventory.getSize(); ++i) {
            if (!this.isLoginItem(playerInventory.getItem(i))) continue;
            playerInventory.setItem(i, null);
        }
        if (this.isLoginItem(playerInventory.getItemInOffHand())) {
            playerInventory.setItemInOffHand(null);
        }
    }

    private ItemStack cubeeItem() {
        return this.systemItem(Material.HONEYCOMB, this.messageText("items.cubee.name", "Cubee"), this.messageText("items.cubee.lore", "green tea"), HoneyPalette.GREEN_TEA, SYSTEM_ITEM_CUBEE);
    }

    private ItemStack loginItem() {
        return this.systemItem(Material.NAME_TAG, this.messageText("items.login.name", "Login"), this.messageText("items.login.lore", "enter passcode."), SYSTEM_ITEM_LOGIN);
    }

    private void setButton(Inventory inventory, Ui.ButtonSpec buttonSpec) {
        inventory.setItem(buttonSpec.slot(), this.menuItem(buttonSpec.material(), buttonSpec.title(), buttonSpec.lore()));
    }

    private void setButton(Inventory inventory, Ui.ButtonSpec buttonSpec, String string) {
        inventory.setItem(buttonSpec.slot(), this.menuItem(buttonSpec.material(), buttonSpec.title(), string));
    }

    private void setButtonLore(Inventory inventory, Ui.ButtonSpec buttonSpec, List<Component> list) {
        inventory.setItem(buttonSpec.slot(), this.menuItemLore(buttonSpec.material(), buttonSpec.title(), list));
    }

    private void setGeneralNav(Inventory inventory, boolean bl, Ui.ButtonSpec buttonSpec) {
        if (buttonSpec != null) {
            this.setButton(inventory, buttonSpec);
        }
        if (bl) {
            this.setButton(inventory, Ui.Shared.NAV_BACK);
        }
    }

    private void setItemLore(ItemStack itemStack, List<Component> list) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(list);
        itemStack.setItemMeta(itemMeta);
    }

    private void setGamemodeButton(Inventory inventory, Ui.ButtonSpec buttonSpec, Player player, GameMode gameMode) {
        this.setButton(inventory, buttonSpec, player.getGameMode() == gameMode ? "current." : null);
    }

    private Material gamemodeMaterial(GameMode gameMode) {
        return switch (gameMode) {
            default -> throw new MatchException(null, null);
            case GameMode.ADVENTURE -> Material.HAY_BLOCK;
            case GameMode.SURVIVAL -> Material.GRASS_BLOCK;
            case GameMode.CREATIVE -> Material.CRAFTING_TABLE;
            case GameMode.SPECTATOR -> Material.GLASS;
        };
    }

    private void clearCareSelfInventory(Player player) {
        if (!this.requireAdmin(player)) {
            return;
        }
        player.closeInventory();
        player.getInventory().clear();
        this.ensureCubee(player, true);
        player.updateInventory();
        Bukkit.getScheduler().runTask((Plugin)this, () -> player.sendMessage((Component)Component.text((String)(this.verifyClearedInventory(player) ? "done." : "try again."), (TextColor)(this.verifyClearedInventory(player) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))));
    }

    private void clearAdminTargetInventory(Player player, Player player2) {
        if (!this.requireAdmin(player)) {
            return;
        }
        if (!this.adminPeopleActionService.canTarget(player, player2)) {
            player.sendMessage((Component)Component.text((String)"nothing changed.", (TextColor)NamedTextColor.DARK_GRAY));
            return;
        }
        player.closeInventory();
        player2.getInventory().clear();
        this.ensureCubee(player2, true);
        player2.updateInventory();
        Bukkit.getScheduler().runTask((Plugin)this, () -> player.sendMessage((Component)Component.text((String)(this.verifyClearedInventory(player2) ? "done." : "try again."), (TextColor)(this.verifyClearedInventory(player2) ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))));
    }

    private boolean verifyClearedInventory(Player player) {
        return this.adminPlayerControlService.verifyClearedInventory(player);
    }

    private void setPlaceButton(Inventory inventory, int n, ServerId serverId) {
        this.setButton(inventory, this.placeTargetSpec(n, serverId));
    }

    private Ui.ButtonSpec placeTargetSpec(int n, ServerId serverId) {
        return new Ui.ButtonSpec(n, this.placeMaterial(serverId), this.placeName(serverId), this.placeTargetLore(serverId));
    }

    private ItemStack menuItem(Material material, String string, String string2) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName((Component)Component.text((String)string, (TextColor)HoneyPalette.DEFAULT_WHITE));
        if (string2 != null) {
            itemMeta.lore(this.loreLines(string2.split("\\R")));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack systemItem(Material material, String string, String string2, String string3) {
        ItemStack itemStack = this.menuItem(material, string, string2);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(this.systemItemKey, PersistentDataType.STRING, string3);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack systemItem(Material material, String string, String string2, TextColor loreColor, String string3) {
        ItemStack itemStack = this.menuItemLore(material, string, string2 == null ? null : List.of(Component.text(string2, loreColor)));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(this.systemItemKey, PersistentDataType.STRING, string3);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack menuItemLore(Material material, String string, List<Component> list) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName((Component)Component.text((String)string, (TextColor)HoneyPalette.DEFAULT_WHITE));
        if (list != null) {
            itemMeta.lore(list);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack playerItem(Player player, String string) {
        return this.menuItem(Material.PLAYER_HEAD, player.getName(), string);
    }

    private boolean isCubee(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.HONEYCOMB || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        return this.hasSystemItemMarker(itemMeta, SYSTEM_ITEM_CUBEE);
    }

    private boolean isSandboxCloneExcludedEntity(Entity entity) {
        if (entity == null || entity instanceof Player) {
            return true;
        }
        for (NamespacedKey key : entity.getPersistentDataContainer().getKeys()) {
            if ("lemonos".equalsIgnoreCase(key.getNamespace())) {
                return true;
            }
        }
        return false;
    }

    private boolean isLoginItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.NAME_TAG || !itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        return this.hasSystemItemMarker(itemMeta, SYSTEM_ITEM_LOGIN) || this.isLegacySystemItem(itemMeta, "Login", "enter passcode.");
    }

    private boolean hasSystemItemMarker(ItemMeta itemMeta, String string) {
        return string.equals(itemMeta.getPersistentDataContainer().get(this.systemItemKey, PersistentDataType.STRING));
    }

    private boolean isLegacySystemItem(ItemMeta itemMeta, String string, String string2) {
        if (!itemMeta.hasDisplayName() || itemMeta.lore() == null || itemMeta.lore().size() != 1) {
            return false;
        }
        return Component.text((String)string, (TextColor)HoneyPalette.DEFAULT_WHITE).equals((Object)itemMeta.displayName()) && Component.text((String)string2, (TextColor)NamedTextColor.GRAY).equals(itemMeta.lore().get(0));
    }

    private boolean isAdmin(Player player) {
        return player != null && (this.proxyAdmins.contains(player.getUniqueId()) || player.hasPermission("lemonos.admin") || player.isOp());
    }

    private boolean isAuthLocked(Player player) {
        return this.authLocked.contains(player.getUniqueId());
    }

    private boolean isBedrockPlayer(Player player) {
        if (Bukkit.getPluginManager().getPlugin("floodgate") == null) {
            return false;
        }
        try {
            Class<?> clazz = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            Object object = clazz.getMethod("getInstance").invoke(null);
            Object object2 = clazz.getMethod("isFloodgatePlayer", UUID.class).invoke(object, player.getUniqueId());
            return object2 instanceof Boolean && (Boolean)object2;
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }

    private ServerId detectServer() {
        int n = this.getServer().getPort();
        if (n == 30066) {
            return ServerId.LOBBY;
        }
        if (n == 25565 || n == 30067) {
            return ServerId.SURVIVAL;
        }
        if (n == 30068) {
            return ServerId.CREATIVE;
        }
        return ServerId.LOBBY;
    }

    private static enum ServerId {
        LOBBY("lobby", "Lobby", "take your time.", 30066),
        SURVIVAL("survival", "Survival", "explore what comes next.", 30067),
        CREATIVE("creative", "Creative", "build what you imagine.", 30068);

        private final String proxyName;
        private final String label;
        private final String lore;
        private final int port;

        private ServerId(String string2, String string3, String string4, int n2) {
            this.proxyName = string2;
            this.label = string3;
            this.lore = string4;
            this.port = n2;
        }
    }

    private static final class ChainState {
        private final BukkitTask task;
        private final Set<String> blocks;
        private final long periodTicks;
        private long nextStepAtMillis;
        private int remaining;

        private ChainState(BukkitTask bukkitTask, Set<String> set, int n, long l) {
            this.task = bukkitTask;
            this.blocks = set;
            this.remaining = Math.max(0, n);
            this.periodTicks = Math.max(1L, l);
            this.nextStepAtMillis = System.nanoTime() / 1_000_000L + this.periodTicks * 50L;
        }
    }

    private static final class StayedCloseRank {
        private final String name;
        private final long totalSeconds;

        private StayedCloseRank(String string, long l) {
            this.name = string;
            this.totalSeconds = l;
        }
    }

    private static final class HudRank {
        private final String name;
        private final long score;

        private HudRank(String string, long l) {
            this.name = string;
            this.score = l;
        }
    }

    private static enum TimePhase {
        MORNING("morning"),
        DAY("day"),
        SUNSET("sunset"),
        NIGHT("night");

        private final String key;

        private TimePhase(String string2) {
            this.key = string2;
        }
    }

    private static enum WeatherPhase {
        CLEAR("clear"),
        RAIN("rain"),
        THUNDER("thunder");

        private final String key;

        private WeatherPhase(String string2) {
            this.key = string2;
        }
    }

    private static final class LoginHolder
    implements InventoryHolder {
        private final IdentityInput input;

        private LoginHolder(IdentityInput identityInput) {
            this.input = identityInput;
        }

        public Inventory getInventory() {
            return null;
        }
    }

    private static final class CubeeHolder
    implements InventoryHolder {
        private final CubeePage page;
        private final Map<Integer, UUID> slotTargets = new HashMap<Integer, UUID>();
        private final Map<Integer, String> slotKeys = new HashMap<Integer, String>();
        private int pageIndex;
        private UUID subject;
        private String resetToken;
        private String accessName;
        private UUID requestSender;
        private RequestKind requestKind;
        private boolean worldExpanded;

        private CubeeHolder(CubeePage cubeePage) {
            this.page = cubeePage;
        }

        public Inventory getInventory() {
            return null;
        }
    }

    private static enum CubeePage {
        HOME,
        GO,
        PEOPLE,
        PLAYER,
        REQUESTS,
        DRAWING,
        CLONE_CONFIRM,
        CLEAR_CONFIRM,
        ROTATE_CONFIRM,
        FLIP_CONFIRM,
        ADMIN,
        ADMIN_PEOPLE,
        ADMIN_REQUESTS,
        ADMIN_RESET,
        ADMIN_PLAYER,
        ADMIN_PLAYER_CONTROL,
        ADMIN_PLAYER_SEND_PLACES,
        ADMIN_PLAYER_CLEAR_CONFIRM,
        ADMIN_KEYS,
        ADMIN_KEY_HOLDERS,
        ADMIN_KEY_HOLDER,
        ADMIN_KEY_TAKE_CONFIRM,
        ADMIN_KEY_PEOPLE,
        ADMIN_KEY_GIVE,
        ADMIN_SELF,
        ADMIN_SELF_CLEAR_CONFIRM,
        ADMIN_ATMOSPHERE,
        ADMIN_UPKEEP,
        ADMIN_BACKUP_CONFIRM,
        ADMIN_CHUNKS,
        ADMIN_CHUNKS_DIMENSION,
        ADMIN_CHUNKS_SIZE,
        ADMIN_CHUNKS_CONFIRM,
        ADMIN_GAMEMODE;

    }

    private static enum IdentityInput {
        CREATE_PASSCODE,
        LOGIN,
        RESET_WAITING;

    }

    private static final class Ui {
        private Ui() {
        }

        private record ButtonSpec(int slot, Material material, String title, String lore) {
        }

        private static final class Home {
            private static final ButtonSpec LOOK = new ButtonSpec(4, Material.ARMOR_STAND, "Look", "change your look.");
            private static final ButtonSpec PEOPLE = new ButtonSpec(12, Material.PLAYER_HEAD, "People", "stay close.");
            private static final ButtonSpec PLACES = new ButtonSpec(13, Material.COMPASS, "Places", "find your way.");
            private static final ButtonSpec HOME = new ButtonSpec(14, Material.RED_BED, "Bed", "return home.");
            private static final ButtonSpec SANDBOX = new ButtonSpec(14, Material.PAINTING, "Sandbox", "make it real.");
            private static final ButtonSpec CARE = new ButtonSpec(22, Material.LANTERN, "Care", "keep the house well.");

            private Home() {
            }
        }

        private static final class Places {
            private Places() {
            }

            private static ButtonSpec target(int n, ServerId serverId, String string) {
                return switch (serverId.ordinal()) {
                    default -> throw new MatchException(null, null);
                    case 0 -> new ButtonSpec(n, Material.OAK_LOG, "Lobby", string);
                    case 1 -> new ButtonSpec(n, Material.GRASS_BLOCK, "Survival", string);
                    case 2 -> new ButtonSpec(n, Material.CRAFTING_TABLE, "Creative", string);
                };
            }
        }

        private static final class Shared {
            private static final ButtonSpec NAV_BACK = new ButtonSpec(0, Material.YELLOW_STAINED_GLASS_PANE, "Back", null);
            private static final ButtonSpec FORM_BACK = new ButtonSpec(22, Material.ARROW, "Back", null);

            private Shared() {
            }
        }

        private static final class Login {
            private static final ButtonSpec ITEM = new ButtonSpec(8, Material.NAME_TAG, "Login", "enter passcode.");
            private static final ButtonSpec CLEAR = new ButtonSpec(9, Material.YELLOW_STAINED_GLASS_PANE, "Clear", "start again.");
            private static final ButtonSpec RESET = new ButtonSpec(25, Material.RED_STAINED_GLASS_PANE, "Reset", "ask for a new code.");
            private static final ButtonSpec ENTER = new ButtonSpec(16, Material.LIME_STAINED_GLASS_PANE, "Sign in", "go in.");
            private static final ButtonSpec CANCEL_RESET = new ButtonSpec(13, Material.RED_STAINED_GLASS_PANE, "Cancel", "keep trying.");

            private Login() {
            }
        }

        private static final class People {
            private static final ButtonSpec FIND = new ButtonSpec(4, Material.SPYGLASS, "Find", "look for someone.");
            private static final ButtonSpec VISIT = new ButtonSpec(12, Material.ENDER_PEARL, "Visit", "meet up.");
            private static final ButtonSpec INVITE = new ButtonSpec(13, Material.ENDER_EYE, "Invite", "bring over.");
            private static final ButtonSpec MESSAGE = new ButtonSpec(14, Material.WRITABLE_BOOK, "Message", "send a note.");

            private People() {
            }
        }

        private static final class Requests {
            private static final ButtonSpec SURE_VISIT = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "Sure", "come in.");
            private static final ButtonSpec SURE_INVITE = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "Sure", "go there.");
            private static final ButtonSpec LATER = new ButtonSpec(12, Material.RED_STAINED_GLASS_PANE, "Later", "not now.");

            private Requests() {
            }
        }

        private static final class Care {
            private static final ButtonSpec FIND = new ButtonSpec(4, Material.SPYGLASS, "Find", "look for someone.");
            private static final ButtonSpec REVIEW = new ButtonSpec(4, Material.SPYGLASS, "Review", "see what's waiting.");
            private static final ButtonSpec HOME = new ButtonSpec(22, Material.WEATHERED_COPPER_LANTERN, "Home", "see what's around.");
            private static final ButtonSpec WORLD = new ButtonSpec(14, Material.MAP, "World", Text.Lore.WORLD);
            private static final ButtonSpec SELF = new ButtonSpec(12, Material.PLAYER_HEAD, null, "look after yourself.");
            private static final ButtonSpec PEOPLE = new ButtonSpec(12, Material.PLAYER_HEAD, "People", null);
            private static final ButtonSpec REQUESTS = new ButtonSpec(4, Material.BELL, "Requests", null);
            private static final ButtonSpec KEYS = new ButtonSpec(13, Material.TRIAL_KEY, "Keys", "keep the house yours.");
            private static final ButtonSpec KEY_PEOPLE = new ButtonSpec(12, Material.PLAYER_HEAD, "Give", "choose who can care.");
            private static final ButtonSpec KEY_HOLDERS = new ButtonSpec(13, Material.SOUL_LANTERN, "Holders", "see who keeps the keys.");
            private static final ButtonSpec KEY_FIND = new ButtonSpec(4, Material.SPYGLASS, "Find", "look through the keys.");
            private static final ButtonSpec CARE_KEY = new ButtonSpec(14, Material.TRIAL_KEY, "Care", "let them care.");
            private static final ButtonSpec TAKE_KEY = new ButtonSpec(13, Material.OMINOUS_TRIAL_KEY, "Take Key", "step them away.");
            private static final ButtonSpec SELF_TAKE_KEY = new ButtonSpec(13, Material.OMINOUS_TRIAL_KEY, "Step Away", "leave the keys.");
            private static final ButtonSpec STEP_AWAY_CONFIRM = new ButtonSpec(14, Material.YELLOW_STAINED_GLASS_PANE, "Step Away", "leave the keys.");
            private static final ButtonSpec STEP_AWAY_PREVIEW = new ButtonSpec(13, Material.OMINOUS_TRIAL_KEY, "Step Away", null);
            private static final ButtonSpec TAKE_KEY_CONFIRM = new ButtonSpec(14, Material.YELLOW_STAINED_GLASS_PANE, "Take Key", "take back the key.");
            private static final ButtonSpec TAKE_KEY_PREVIEW = new ButtonSpec(13, Material.OMINOUS_TRIAL_KEY, "Take Key", null);
            private static final ButtonSpec ATMOSPHERE = new ButtonSpec(15, Material.CANDLE, "Atmosphere", "make it feel right.");
            private static final ButtonSpec PLACES = new ButtonSpec(13, Material.MAP, "Places", null);
            private static final ButtonSpec UPKEEP = new ButtonSpec(16, Material.CARTOGRAPHY_TABLE, "Upkeep", "keep the world ready.");
            private static final ButtonSpec BACKUP = new ButtonSpec(12, Material.PAPER, "Backup", "keep a copy.");
            private static final ButtonSpec BACKUP_CONFIRM = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "Backup", "keep a copy.");
            private static final ButtonSpec CHUNKS = new ButtonSpec(13, Material.BEACON, "Chunks", "prepare the world.");
            private static final ButtonSpec CHUNKS_START = new ButtonSpec(15, Material.LIME_STAINED_GLASS_PANE, "Start", "prepare the world.");
            private static final ButtonSpec CHUNKS_CANCEL = new ButtonSpec(14, Material.RED_STAINED_GLASS_PANE, "Cancel", "stop preparing.");
            private static final ButtonSpec CHUNKS_SIZE = new ButtonSpec(12, Material.SPYGLASS, "Size", "choose how far.");
            private static final ButtonSpec CHUNKS_CENTER = new ButtonSpec(11, Material.TARGET, "Center", "start from here.");
            private static final ButtonSpec CHUNKS_CONFIRM = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "Start", "prepare now.");

            private Care() {
            }
        }

        private static final class Chunks {
            private static final ButtonSpec WORLD = new ButtonSpec(12, Material.GRASS_BLOCK, "World", null);
            private static final ButtonSpec NETHER = new ButtonSpec(13, Material.NETHER_BRICKS, "Nether", null);
            private static final ButtonSpec THE_END = new ButtonSpec(14, Material.END_STONE_BRICKS, "The End", null);
            private static final ButtonSpec SIZE_1500 = new ButtonSpec(12, Material.GLASS, "1500", null);
            private static final ButtonSpec SIZE_3000 = new ButtonSpec(13, Material.GLASS, "3000", null);
            private static final ButtonSpec SIZE_5000 = new ButtonSpec(14, Material.GLASS, "5000", null);

            private Chunks() {
            }
        }

        private static final class CarePlayer {
            private static final ButtonSpec VISIT = new ButtonSpec(12, Material.ENDER_PEARL, "Visit", "go there.");
            private static final ButtonSpec INVITE = new ButtonSpec(13, Material.ENDER_EYE, "Invite", "bring here.");
            private static final ButtonSpec MESSAGE = new ButtonSpec(14, Material.WRITABLE_BOOK, "Message", "send a note.");
            private static final ButtonSpec SEND = new ButtonSpec(14, Material.COMPASS, "Send", "send them on.");

            private CarePlayer() {
            }
        }

        private static final class Reset {
            private static final ButtonSpec ALLOW = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "allow", "let them reset.");
            private static final ButtonSpec NOT_NOW = new ButtonSpec(12, Material.RED_STAINED_GLASS_PANE, "not now", null);

            private Reset() {
            }
        }

        private static final class CareSelf {
            private static final ButtonSpec GAMEMODE = new ButtonSpec(12, Material.GRASS_BLOCK, "Mode", "choose your mode.");
            private static final ButtonSpec CLEAR = new ButtonSpec(13, Material.TINTED_GLASS, "Clear", "clear your things.");
            private static final ButtonSpec CLEAR_CONFIRM = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "Clear", "clear your things.");
            private static final ButtonSpec TARGET_CLEAR = new ButtonSpec(13, Material.TINTED_GLASS, "Clear", "clear their things.");
            private static final ButtonSpec TARGET_CLEAR_CONFIRM = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "Clear", "clear their things.");
            private static final ButtonSpec SELF_PREVIEW = new ButtonSpec(13, Material.TINTED_GLASS, "Clear", null);
            private static final ButtonSpec CANCEL = new ButtonSpec(12, Material.RED_STAINED_GLASS_PANE, "Cancel", null);

            private CareSelf() {
            }
        }

        private static final class Atmosphere {
            private static final ButtonSpec DAY = new ButtonSpec(11, Material.YELLOW_CANDLE, "Day", "more light.");
            private static final ButtonSpec NIGHT = new ButtonSpec(12, Material.BLUE_CANDLE, "Night", "more stars.");
            private static final ButtonSpec RAIN = new ButtonSpec(13, Material.LIGHT_BLUE_CANDLE, "Rain", "more rain.");
            private static final ButtonSpec CLEAR = new ButtonSpec(14, Material.WHITE_CANDLE, "Clear", "clear skies.");

            private Atmosphere() {
            }
        }

        private static final class Gamemode {
            private static final ButtonSpec ADVENTURE = new ButtonSpec(11, Material.HAY_BLOCK, "Adventure", null);
            private static final ButtonSpec SURVIVAL = new ButtonSpec(12, Material.GRASS_BLOCK, "Survival", null);
            private static final ButtonSpec CREATIVE = new ButtonSpec(13, Material.CRAFTING_TABLE, "Creative", null);
            private static final ButtonSpec SPECTATOR = new ButtonSpec(14, Material.GLASS, "Spectator", null);

            private Gamemode() {
            }
        }

        private static final class Sandbox {
            private static final ButtonSpec BACK = new ButtonSpec(0, Material.YELLOW_STAINED_GLASS_PANE, "Back", null);
            private static final ButtonSpec UNDO = new ButtonSpec(1, Material.ARROW, "Undo", "step back.");
            private static final ButtonSpec REDO = new ButtonSpec(2, Material.SPECTRAL_ARROW, "Redo", "bring it back.");
            private static final ButtonSpec SET = new ButtonSpec(3, Material.TARGET, "Set", "fill the canvas.");
            private static final ButtonSpec WALL = new ButtonSpec(4, Material.BRICKS, "Wall", "raise your outline.");
            private static final ButtonSpec FLOOR = new ButtonSpec(5, Material.OAK_PLANKS, "Floor", "set the ground.");
            private static final ButtonSpec REPLACE = new ButtonSpec(12, Material.PINK_GLAZED_TERRACOTTA, "Replace", "change the material.");
            private static final ButtonSpec CLONE = new ButtonSpec(13, Material.WHITE_SHULKER_BOX, "Clone", "copy and place.");
            private static final ButtonSpec CLEAR = new ButtonSpec(14, Material.TINTED_GLASS, "Clear", "clear the canvas.");
            private static final ButtonSpec CIRCLE = new ButtonSpec(21, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, "Circle", "make it round.");
            private static final ButtonSpec FLIP = new ButtonSpec(22, Material.LIME_GLAZED_TERRACOTTA, "Flip", "turn it over.");
            private static final ButtonSpec ROTATE = new ButtonSpec(23, Material.MAGENTA_GLAZED_TERRACOTTA, "Rotate", "turn it around.");

            private Sandbox() {
            }
        }

        private static final class Confirm {
            private static final ButtonSpec PLACE = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "Place", "place here.");
            private static final ButtonSpec CANCEL = new ButtonSpec(12, Material.RED_STAINED_GLASS_PANE, "Cancel", null);
            private static final ButtonSpec CLONE_PREVIEW = new ButtonSpec(13, Material.WHITE_SHULKER_BOX, "Clone", null);
            private static final ButtonSpec CLEAR = new ButtonSpec(14, Material.LIME_STAINED_GLASS_PANE, "Clear", "clear this area.");
            private static final ButtonSpec CLEAR_PREVIEW = new ButtonSpec(13, Material.TINTED_GLASS, "Clear", null);
            private static final ButtonSpec ROTATE_PREVIEW = new ButtonSpec(13, Material.MAGENTA_GLAZED_TERRACOTTA, "Rotate", null);
            private static final ButtonSpec FLIP_PREVIEW = new ButtonSpec(13, Material.LIME_GLAZED_TERRACOTTA, "Flip", null);

            private Confirm() {
            }
        }
    }

    private record PendingSkinApply(String skin, boolean shouldNotify) {
    }

    private static final class AdminSendOperation {
        private final UUID actorId;
        private final UUID targetId;
        private final ServerId destination;
        private final BackendOperationToken token;
        private final BackendOperationTaskSlot taskSlot = new BackendOperationTaskSlot();
        private final BackendOperationStatusLease statusLease;

        private AdminSendOperation(UUID actorId, UUID targetId, ServerId destination, BackendOperationToken token, BackendOperationStatusLease statusLease) {
            this.actorId = actorId;
            this.targetId = targetId;
            this.destination = destination;
            this.token = token;
            this.statusLease = statusLease;
        }
    }

    private static final class ManualBackupOperation {
        private final UUID initiator;
        private final BackendOperationToken token;
        private final Set<UUID> participants = ConcurrentHashMap.newKeySet();
        private final BackendOperationTaskSlot workerTask = new BackendOperationTaskSlot();
        private final BackendOperationTaskSlot timeoutTask = new BackendOperationTaskSlot();
        private final BackendOperationCancellation cancellation = new BackendOperationCancellation();

        private ManualBackupOperation(UUID initiator, BackendOperationToken token) {
            this.initiator = initiator;
            this.token = token;
        }
    }

    private static final class ChunkOperation {
        private final String world;
        private final UUID actor;
        private final BackendOperationToken token;
        private int stoppedConfirmations;
        private int probeFailures;
        private boolean completionVerificationActive;

        private ChunkOperation(String world, UUID actor, BackendOperationToken token) {
            this.world = world;
            this.actor = actor;
            this.token = token;
        }
    }

    private static enum ChunkDimension {
        WORLD("world", "world.", "world", Material.GRASS_BLOCK),
        NETHER("nether", "nether.", "world_nether", Material.NETHER_BRICKS),
        THE_END("the_end", "the end.", "world_the_end", Material.END_STONE_BRICKS);

        private final String key;
        private final String lore;
        private final String bukkitWorldName;
        private final Material material;

        private ChunkDimension(String string2, String string3, String string4, Material material) {
            this.key = string2;
            this.lore = string3;
            this.bukkitWorldName = string4;
            this.material = material;
        }

        private static ChunkDimension fromKey(String string) {
            for (ChunkDimension chunkDimension : ChunkDimension.values()) {
                if (!chunkDimension.key.equalsIgnoreCase(String.valueOf(string))) continue;
                return chunkDimension;
            }
            return WORLD;
        }
    }

    private static enum RequestKind {
        VISIT,
        INVITE;

    }

    private static enum DrawingAction {
        SET,
        WALL,
        FLOOR,
        CLEAR,
        CLONE,
        CIRCLE,
        REPLACE,
        FLIP,
        ROTATE;

    }

    private static final class Text {
        private Text() {
        }

        private static final class Title {
            private static final String CUBEE = "Cubee";
            private static final String HOME = "Home";
            private static final String PLACES = "Places";
            private static final String PEOPLE = "People";
            private static final String REQUESTS = "Requests";
            private static final String SANDBOX = "Sandbox";
            private static final String CLONE = "Clone";
            private static final String CLEAR = "Clear";
            private static final String ROTATE = "Rotate";
            private static final String FLIP = "Flip";
            private static final String CARE = "Care";
            private static final String KEYS = "Keys";
            private static final String WORLD = "World";
            private static final String UPKEEP = "Upkeep";
            private static final String BACKUP = "Backup";
            private static final String CHUNKS = "Chunks";
            private static final String DIMENSION = "Dimension";
            private static final String SIZE = "Size";
            private static final String CENTER = "Center";
            private static final String ATMOSPHERE = "Atmosphere";
            private static final String GAMEMODE = "Mode";
            private static final String RESET = "Reset";
            private static final String HOLDERS = "Holders";
            private static final String STEP_AWAY = "Step Away";
            private static final String GIVE = "Give";
            private static final String TAKE_KEY = "Take Key";
            private static final String LOOK = "Look";
            private static final String CREATE_PASSCODE = "Create Passcode";
            private static final String ENTER_PASSCODE = "Enter Passcode";
            private static final String LOGIN = "Login";
            private static final String WAITING = "Waiting";

            private Title() {
            }
        }

        private static final class ConfirmTitle {
            private static final String PLACE_IT_HERE = "Place it here?";
            private static final String CLEAR_THIS_AREA = "Clear this area?";
            private static final String PLACE_IT_TURNED = "Place it turned?";
            private static final String PLACE_IT_MIRRORED = "Place it mirrored?";
            private static final String LET_THEM_CARE = "Let them care?";
            private static final String TAKE_THEIR_KEY = "Take their key?";
            private static final String LEAVE_THE_KEYS = "Leave the keys?";
            private static final String CLEAR_YOUR_THINGS = "Clear your things?";
            private static final String CLEAR_THEIR_THINGS = "Clear their things?";
            private static final String LET_THEM_RESET = "Let them reset?";
            private static final String LET_THEM_VISIT = "Let them visit?";
            private static final String GO_WITH_THEM = "Go with them?";
            private static final String KEEP_A_COPY = "Keep a copy?";
            private static final String PREPARE_THE_WORLD = "Prepare the world?";

            private ConfirmTitle() {
            }
        }

        private static final class Label {
            private static final String LOBBY = "Lobby";
            private static final String SURVIVAL = "Survival";
            private static final String CREATIVE = "Creative";
            private static final String SET = "Fill";
            private static final String WALL = "Wall";
            private static final String FLOOR = "Floor";
            private static final String CIRCLE = "Circle";
            private static final String REPLACE = "Replace";
            private static final String DAY = "Day";
            private static final String NIGHT = "Night";
            private static final String RAIN = "Rain";
            private static final String WEATHER_CLEAR = "Clear";
            private static final String ADVENTURE = "Adventure";
            private static final String SURVIVAL_MODE = "Survival";
            private static final String CREATIVE_MODE = "Creative";
            private static final String SPECTATOR = "Spectator";
            private static final String DEFAULT = "Default";
            private static final String BLUE = "Blue";
            private static final String PINK = "Pink";
            private static final String ORANGE = "Orange";
            private static final String GREEN = "Green";
            private static final String START = "Start";
            private static final String WORLD = "World";
            private static final String NETHER = "Nether";
            private static final String THE_END = "The End";

            private Label() {
            }
        }

        private static final class Lore {
            private static final String CUBEE = "green tea";
            private static final String PEOPLE = "stay close.";
            private static final String PLACES = "find your way.";
            private static final String LOOK = "change your look.";
            private static final String SEE_WHATS_AROUND = "see what's around.";
            private static final String LOOK_AFTER_YOURSELF = "look after yourself.";
            private static final String LOOK_AFTER_THEM = "look after them.";
            private static final String FOR_YOU = "for you.";
            private static final String CLEAR_YOUR_THINGS = "clear your things.";
            private static final String CLEAR_THEIR_THINGS = "clear their things.";
            private static final String YOU = "you.";
            private static final String LOBBY = "take your time.";
            private static final String SURVIVAL = "explore what comes next.";
            private static final String CREATIVE = "build what you imagine.";
            private static final String SANDBOX = "make it real.";
            private static final String CARE = "keep the house well.";
            private static final String KEYS = "keep the house yours.";
            private static final String HOLDERS = "see who keeps the keys.";
            private static final String KEY_PEOPLE = "choose who can care.";
            private static final String STEP_AWAY = "leave the keys.";
            private static final String LOOK_THROUGH_KEYS = "look through the keys.";
            private static final String LET_THEM_CARE = "let them care.";
            private static final String TAKE_KEY = "step them away.";
            private static final String TAKE_BACK_KEY = "take back the key.";
            private static final String HOLDS_KEY = "holds a key.";
            private static final String YOU_HOLD_KEY = "you hold a key.";
            private static final String LET_THEM_RESET = "let them reset.";
            private static final String THEIR_THINGS = "their things.";
            private static final String WORLD = null;
            private static final String UPKEEP = "keep the world ready.";
            private static final String KEEP_A_COPY = "keep a copy.";
            private static final String NO_COPY_YET = "no copy yet.";
            private static final String CHUNKS = "prepare the world.";
            private static final String STOP_PREPARING = "stop preparing.";
            private static final String CHOOSE_WHERE = "choose where.";
            private static final String CHOOSE_HOW_FAR = "choose how far.";
            private static final String START_FROM_HERE = "start from here.";
            private static final String PREPARE_NOW = "prepare now.";
            private static final String ATMOSPHERE = "make it feel right.";
            private static final String UNDO = "step back.";
            private static final String REDO = "bring it back.";
            private static final String BUSY = "busy.";
            private static final String CURRENT = "current.";
            private static final String PUBLIC = "public.";
            private static final String PRIVATE = "private.";
            private static final String KEEP = "keep.";
            private static final String ONCE = "once.";
            private static final String HIDE = "hide.";
            private static final String SHOW = "show.";
            private static final String MEET_UP = "meet up.";
            private static final String BRING_OVER = "bring over.";
            private static final String GO_THERE = "go there.";
            private static final String BRING_HERE = "bring here.";
            private static final String KEEP_CLOSE = "keep close.";
            private static final String CHOOSE_MODE = "choose your mode.";
            private static final String CHOOSE_THEIR_MODE = "choose their mode.";
            private static final String FIND = "look for someone.";
            private static final String REVIEW = "see what's waiting.";
            private static final String SEND_NOTE = "send a note.";
            private static final String SET = "fill the canvas.";
            private static final String WALL = "raise your outline.";
            private static final String FLOOR = "set the ground.";
            private static final String CLONE = "copy and place.";
            private static final String CLEAR = "clear the canvas.";
            private static final String CIRCLE = "make it round.";
            private static final String REPLACE = "change the material.";
            private static final String FLIP = "turn it over.";
            private static final String ROTATE = "turn it around.";
            private static final String PLACE = "place here.";
            private static final String CLEAR_CONFIRM = "clear this area.";
            private static final String DAY = "more light.";
            private static final String NIGHT = "more stars.";
            private static final String RAIN = "more rain.";
            private static final String WEATHER_CLEAR = "clear skies.";
            private static final String LOGIN = "enter passcode.";
            private static final String PASSCODE_LENGTH = "4-8 numbers";
            private static final String START_AGAIN = "start again.";
            private static final String ASK_NEW_CODE = "ask for a new code.";
            private static final String GO_IN = "go in.";
            private static final String KEEP_NEARBY = "keep nearby.";
            private static final String KEEP_TRYING = "keep trying.";

            private Lore() {
            }
        }

        private static final class Button {
            private static final String CLOSE = "Close";
            private static final String BACK = "Back";
            private static final String PLACE = "Place";
            private static final String CANCEL = "Cancel";
            private static final String UNDO = "Undo";
            private static final String REDO = "Redo";
            private static final String SURE = "Sure";
            private static final String LATER = "Later";
            private static final String ALLOW = "allow";
            private static final String NOT_NOW = "not now";
            private static final String VISIT = "Visit";
            private static final String INVITE = "Invite";
            private static final String FIND = "Find";
            private static final String REVIEW = "Review";
            private static final String MESSAGE = "Message";
            private static final String CLEAR = "Clear";
            private static final String ENTER = "Enter";
            private static final String RESET = "Reset";

            private Button() {
            }
        }

        private static final class Result {
            private static final String DONE = "done.";
            private static final String SAVED = "saved.";
            private static final String LOGIN = "you're in.";
            private static final String TRANSFER_START = "on the way.";
            private static final String TRANSFER_ARRIVE = "you're here.";
            private static final String CANCEL = "nothing changed.";
            private static final String EXPIRED = "too late.";
            private static final String INVALID = "try again.";
            private static final String TOO_SHORT = "too short.";
            private static final String TOO_LONG = "too long.";
            private static final String TOO_LARGE = "too large.";
            private static final String VERIFYING = "verifying.";
            private static final String WAITING = "waiting.";
            private static final String NOT_AVAILABLE = "out of range.";
            private static final String SENT = "sent.";
            private static final String MAKE_SPACE = "make space in Slot 9.";

            private Result() {
            }
        }

        private static final class Prompt {
            private static final String LOGIN_CREATE = "Create Passcode";
            private static final String LOGIN_ENTER = "Enter Passcode";
            private static final String FIRST_CORNER_TITLE = "First corner.";
            private static final String FIRST_CORNER_GUIDANCE = "Start the shape.";
            private static final String SECOND_CORNER_TITLE = "Second corner.";
            private static final String SECOND_CORNER_GUIDANCE = "Finish the shape.";
            private static final String CURRENT_BLOCK_TITLE = "Current block.";
            private static final String CURRENT_BLOCK_GUIDANCE = "Pick what to replace.";
            private static final String NEW_BLOCK_TITLE = "New block.";
            private static final String NEW_BLOCK_GUIDANCE = "Pick what it becomes.";
            private static final String SIZE_TITLE = "Size.";
            private static final String SIZE_GUIDANCE = "Type 1-16.";
            private static final String PLACE_TITLE = "Place.";
            private static final String PLACE_GUIDANCE = "Choose where it lands.";
            private static final String BLOCK_TITLE = "Block.";
            private static final String BLOCK_GUIDANCE = "Choose what it becomes.";
            private static final String ROTATE_TITLE = "Rotate.";
            private static final String ROTATE_GUIDANCE = "Type 90, 180, or 270.";
            private static final String FLIP_TITLE = "Flip.";
            private static final String FLIP_GUIDANCE = "Type x or z.";
            private static final String CENTER_TITLE = "Center.";
            private static final String CENTER_GUIDANCE = "Pick the middle point.";
            private static final String NAME_TITLE = "Name.";
            private static final String NAME_GUIDANCE = "Choose a look.";
            private static final String WRITE_NOTE_GUIDANCE = "Write a note.";
            private static final String TOOL_AXE = "Use wooden axe.";
            private static final String TOOL_HOE = "Use wooden hoe.";

            private Prompt() {
            }
        }

        private static final class Status {
            private static final String AVAILABLE = "available.";
            private static final String NOT_READY = "not ready yet.";
            private static final String UNAVAILABLE = "no signal.";
            private static final String IDLE = "idle.";
            private static final String RUNNING = "running.";
            private static final String READY = "Ready. open cubee.";
            private static final String OPEN = "open.";
            private static final String CLOSED = "closed.";
            private static final String NOBODY_HERE = "nobody here.";
            private static final String NO_HOLDERS = "no holders.";

            private Status() {
            }
        }

        private static final class Sentence {
            private static final String HERE = "here.";
            private static final String IS_HERE = "is here.";
            private static final String WANTS_TO_VISIT = "wants to visit you.";
            private static final String INVITES_YOU = "invites you.";
            private static final String OPEN_CUBEE = "open cubee.";
            private static final String HELP = "/cubee";
            private static final String HELP_CUBEE = "open cubee.";
            private static final String OPEN_REQUESTS = "check requests.";
            private static final String CARE_NEEDS_YOU = "The house needs care.";
            private static final String WANTS_TO_RESET = "wants to reset.";
            private static final String COME_IN = "come in.";
            private static final String NOT_NOW = "not now.";

            private Sentence() {
            }
        }

        private static final class Health {
            private static final String TPS = " tps";
            private static final String MS = " ms";
            private static final String LOAD = "% load";
            private static final String LOAD_UNAVAILABLE = "load unavailable.";
            private static final String CHUNKS = " chunks";
            private static final String ENTITIES = " entities";
            private static final String GB = " GB";
            private static final String GB_FREE = " GB free";

            private Health() {
            }
        }
    }

    private static enum ConnectionMode {
        ALL,
        FACE,
        FLAT;

    }

    private static enum ChainType {
        TREE,
        VEIN,
        PLANT,
        VERTICAL_PLANT;

    }

    private static final class FrontierChoice {
        private final Block anchor;
        private final List<Block> blocks;

        private FrontierChoice(Block block, List<Block> list) {
            this.anchor = block;
            this.blocks = list;
        }
    }

    private static final class DrawingState {
        private final DrawingAction action;
        private Location first;
        private Location second;
        private BlockData blockData;
        private BlockData sourceBlockData;
        private int failedInputs;
        private DrawingInputStep inputStep = DrawingInputStep.NONE;
        private int size;
        private int rotation;
        private char flipAxis;
        private boolean repeatDoneShown;

        private DrawingState(DrawingAction drawingAction) {
            this.action = drawingAction;
        }
    }

    private static enum DrawingInputStep {
        NONE,
        SIZE,
        CIRCLE_BLOCK,
        REPLACE_SOURCE,
        NEW_BLOCK,
        FLIP,
        ROTATION,
        READY;

    }

    private static final class DrawingChange {
        private final List<BlockChange> blocks = new ArrayList<BlockChange>();
        private final List<EntityChange> entities = new ArrayList<EntityChange>();

        private DrawingChange() {
        }

        private boolean isEmpty() {
            return this.blocks.isEmpty() && this.entities.isEmpty();
        }
    }

    private static final class EntityChange {
        private final World world;
        private final UUID sourceId;
        private final UUID vehicleSourceId;
        private final Location target;
        private final EntitySnapshot snapshot;
        private UUID spawnedId;

        private EntityChange(World world, UUID sourceId, UUID vehicleSourceId, Location target, EntitySnapshot snapshot) {
            this.world = world;
            this.sourceId = sourceId;
            this.vehicleSourceId = vehicleSourceId;
            this.target = target;
            this.snapshot = snapshot;
        }
    }

    private static final class BlockChange {
        private final World world;
        private final int x;
        private final int y;
        private final int z;
        private final BlockData oldData;
        private final BlockData newData;

        private BlockChange(World world, int n, int n2, int n3, BlockData blockData, BlockData blockData2) {
            this.world = world;
            this.x = n;
            this.y = n2;
            this.z = n3;
            this.oldData = blockData;
            this.newData = blockData2;
        }
    }

    private static final class BlockPosition {
        private final int x;
        private final int y;
        private final int z;

        private BlockPosition(int n, int n2, int n3) {
            this.x = n;
            this.y = n2;
            this.z = n3;
        }

        public boolean equals(Object object) {
            if (!(object instanceof BlockPosition)) {
                return false;
            }
            BlockPosition blockPosition = (BlockPosition)object;
            return this.x == blockPosition.x && this.y == blockPosition.y && this.z == blockPosition.z;
        }

        public int hashCode() {
            return Objects.hash(this.x, this.y, this.z);
        }
    }

    private static final class RotatePreview {
        private final World world;
        private final int minX;
        private final int minY;
        private final int minZ;
        private final int maxX;
        private final int maxY;
        private final int maxZ;
        private final DrawingChange change;

        private RotatePreview(World world, int n, int n2, int n3, int n4, int n5, int n6, DrawingChange drawingChange) {
            this.world = world;
            this.minX = n;
            this.minY = n2;
            this.minZ = n3;
            this.maxX = n4;
            this.maxY = n5;
            this.maxZ = n6;
            this.change = drawingChange;
        }
    }

    private static final class ClonePreview {
        private final World world;
        private final int minX;
        private final int minY;
        private final int minZ;
        private final int maxX;
        private final int maxY;
        private final int maxZ;
        private final int movesLeft;
        private final List<CloneBlock> blocks = new ArrayList<CloneBlock>();
        private final List<CloneEntity> entities = new ArrayList<CloneEntity>();

        private ClonePreview(World world, int n, int n2, int n3, int n4, int n5, int n6, int movesLeft) {
            this.world = world;
            this.minX = n;
            this.minY = n2;
            this.minZ = n3;
            this.maxX = n4;
            this.maxY = n5;
            this.maxZ = n6;
            this.movesLeft = movesLeft;
        }
    }

    private static final class CloneEntity {
        private final UUID sourceId;
        private final UUID vehicleSourceId;
        private final double offsetX;
        private final double offsetY;
        private final double offsetZ;
        private final float yaw;
        private final float pitch;
        private final EntitySnapshot snapshot;

        private CloneEntity(UUID sourceId, UUID vehicleSourceId, double offsetX, double offsetY, double offsetZ, float yaw, float pitch, EntitySnapshot snapshot) {
            this.sourceId = sourceId;
            this.vehicleSourceId = vehicleSourceId;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.yaw = yaw;
            this.pitch = pitch;
            this.snapshot = snapshot;
        }
    }

    private static final class CloneBlock {
        private final int offsetX;
        private final int offsetY;
        private final int offsetZ;
        private final BlockData blockData;

        private CloneBlock(int n, int n2, int n3, BlockData blockData) {
            this.offsetX = n;
            this.offsetY = n2;
            this.offsetZ = n3;
            this.blockData = blockData;
        }
    }

    private static final class ClearPreview {
        private final World world;
        private final int minX;
        private final int minY;
        private final int minZ;
        private final int maxX;
        private final int maxY;
        private final int maxZ;

        private ClearPreview(World world, int n, int n2, int n3, int n4, int n5, int n6) {
            this.world = world;
            this.minX = n;
            this.minY = n2;
            this.minZ = n3;
            this.maxX = n4;
            this.maxY = n5;
            this.maxZ = n6;
        }
    }

    private static final class SavedInventory {
        private final ItemStack[] contents;
        private final ItemStack[] armor;
        private final ItemStack offhand;
        private final int heldSlot;

        private SavedInventory(PlayerInventory playerInventory) {
            this.contents = SavedInventory.cloneItems(playerInventory.getContents());
            this.armor = SavedInventory.cloneItems(playerInventory.getArmorContents());
            this.offhand = SavedInventory.cloneItem(playerInventory.getItemInOffHand());
            this.heldSlot = playerInventory.getHeldItemSlot();
        }

        private static ItemStack[] cloneItems(ItemStack[] itemStackArray) {
            ItemStack[] itemStackArray2 = new ItemStack[itemStackArray.length];
            for (int i = 0; i < itemStackArray.length; ++i) {
                itemStackArray2[i] = SavedInventory.cloneItem(itemStackArray[i]);
            }
            return itemStackArray2;
        }

        private static ItemStack cloneItem(ItemStack itemStack) {
            return itemStack == null ? null : itemStack.clone();
        }
    }
}
