package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PlayerEntity::class, MatchEntity::class, MatchPlayerEntity::class],
    version = 4,
    exportSchema = false
)
abstract class KooraDatabase : RoomDatabase() {

    abstract fun kooraDao(): KooraDao

    companion object {
        @Volatile
        private var INSTANCE: KooraDatabase? = null

        // ترحيل يضيف عمود matchTimestamp لجدول matches بدون حذف أي بيانات قديمة
        // (سجلات الأصدقاء ونتائج المباريات السابقة تبقى محفوظة). المباريات القديمة التي
        // لا تملك تاريخاً فعلياً محفوظاً يُنسب لها وقت الترحيل نفسه كأقرب تقدير معقول.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE matches ADD COLUMN matchTimestamp INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}"
                )
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): KooraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KooraDatabase::class.java,
                    "koora_friday_db"
                )
                    .addMigrations(MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .addCallback(KooraDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class KooraDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDefaultData(database.kooraDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    if (database.kooraDao().getPlayersCount() == 0) {
                        populateDefaultData(database.kooraDao())
                    }
                }
            }
        }

        suspend fun populateDefaultData(dao: KooraDao) {
            val defaultPlayers = listOf(
                PlayerEntity(
                    id = 1, name = "هاني عبد الخالق", nickname = "السلطان", position = "Forward",
                    level = "Level 3", favoriteClub = "الأهلي",
                    topTraits = "هداف, مهاري, خلوق, يكره الهزيمة", avatarType = "sultan",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 2, name = "أيمن محمد", nickname = "العميد", position = "Forward",
                    level = "Level 3", favoriteClub = "الأهلي",
                    topTraits = "مهاجم مخضرم, ضربات رأس, ذكي جداً", avatarType = "ameed",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 3, name = "حمادة عبد الفتاح", nickname = "الشيخ حمادة", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الزمالك",
                    topTraits = "قوي بدنياً, زملكاوي غير متحيز", avatarType = "sheikh",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 4, name = "محمد عبد اللاه", nickname = "الأسطورة", position = "Defender",
                    level = "Level 1", favoriteClub = "الأهلي",
                    topTraits = "قراءة ممتازة للعب, قوي في الالتحامات, كرات عالية", avatarType = "ostora",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 5, name = "كريم صلاح", nickname = "الأسد", position = "Defender",
                    level = "Level 1", favoriteClub = "الأهلي",
                    topTraits = "قوي, عنيف, أحياناً متهور", avatarType = "asad",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 6, name = "أحمد مصطفى", nickname = "مالديني", position = "Defender",
                    level = "Level 2", favoriteClub = "الزمالك",
                    topTraits = "مرح, كوميدي, يجيد الكرات الأرضية", avatarType = "maldini",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 7, name = "عماد صلاح", nickname = "العمدة", position = "Defender",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "يجيد التسديد البعيد, مرح", avatarType = "omda",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 8, name = "محمد حلمي", nickname = "حلمي", position = "Defender",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "أاهلاوي, يجيد العرضيات, يجيد التسديد", avatarType = "helmy",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 9, name = "وجدي", nickname = "رونالدينيو", position = "Midfielder",
                    level = "Level 1", favoriteClub = "الزمالك",
                    topTraits = "مهاري, تسديد قوي, ركلات ثابتة, زملكاوي", avatarType = "ronaldinho",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 10, name = "السيد صديق", nickname = "بوبو", position = "Forward",
                    level = "Level 1", favoriteClub = "الأهلي",
                    topTraits = "هداف, يجيد صناعة اللعب, تسديد بسن القدم", avatarType = "bubu",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 11, name = "محمد مصطفى", nickname = "تريكة", position = "Forward",
                    level = "Level 1", favoriteClub = "الأهلي",
                    topTraits = "مهاري, هداف, يجيد اللعب بكلتا القدمين", avatarType = "treka",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 12, name = "محمد عبد اللطيف", nickname = "مو تيفا", position = "Forward",
                    level = "Level 1", favoriteClub = "الأهلي",
                    topTraits = "سريع, هداف, يجيد صناعة اللعب, بكلتا القدمين", avatarType = "motifa",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 13, name = "محمد عبد الفتاح", nickname = "تاحا", position = "Goalkeeper",
                    level = "N/A", favoriteClub = "الأهلي",
                    topTraits = "حارس مرمى قوي, رد فعل سريع", avatarType = "taja",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 14, name = "الشناوي", nickname = "شنو", position = "Goalkeeper",
                    level = "N/A", favoriteClub = "الأهلي",
                    topTraits = "حارس مرمى عملاق, شباك نظيفة", avatarType = "asad_aali",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 15, name = "أحمد هاني", nickname = "لوفي", position = "Midfielder",
                    level = "Level 1", favoriteClub = "الأهلي",
                    topTraits = "شاب, قوي دفاعياً, لياقة عالية", avatarType = "ahmed_hany",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 16, name = "مصطفى", nickname = "درش", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الزمالك",
                    topTraits = "تسديد قوي, صناعة لعب, زملكاوي", avatarType = "darsh",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 17, name = "ديسكا", nickname = "الفهد الأسمر", position = "Forward",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "مهاجم سريع, مهاري, الفهد الأسمر", avatarType = "sultan",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 18, name = "فيجو", nickname = "الفنان فيجو", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الزمالك",
                    topTraits = "لاعب وسط ممتع, تمريرات دقيقة, زملكاوي", avatarType = "sheikh",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 19, name = "مدحت", nickname = "الفيلسوف مدحت", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "فيلسوف الملعب, رؤية ممتازة, هادئ", avatarType = "darsh",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 20, name = "إسلام", nickname = "دام روما", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الزمالك",
                    topTraits = "لياقة عالية, تسديدات قوية, زملكاوي", avatarType = "ahmed_hany",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 21, name = "إسلام السويركي", nickname = "السويركي", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "صانع ألعاب, مراوغ, أاهلاوي", avatarType = "ronaldinho",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 22, name = "وليد", nickname = "الوحش", position = "Goalkeeper",
                    level = "N/A", favoriteClub = "الأهلي",
                    topTraits = "حارس مرمى شجاع, رد فعل ممتاز", avatarType = "taja",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 23, name = "سامح", nickname = "الساحر", position = "Forward",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "مهاجم, لمسة سحرية", avatarType = "bubu",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 24, name = "زياد", nickname = "الجناح", position = "Forward",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "سريع, انطلاقات قوية", avatarType = "motifa",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 25, name = "طارق", nickname = "الصخرة", position = "Defender",
                    level = "Level 2", favoriteClub = "الزمالك",
                    topTraits = "مدافع قوي, قطع كرات", avatarType = "asad",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 26, name = "حازم", nickname = "البرنس", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "تمريرات حاسمة, تحكم بالكرة", avatarType = "omda",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 27, name = "عمر", nickname = "الدينامو", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الزمالك",
                    topTraits = "مجهود وافر, تغطية دفاعية", avatarType = "maldini",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 28, name = "خالد", nickname = "السد العالي", position = "Goalkeeper",
                    level = "N/A", favoriteClub = "الزمالك",
                    topTraits = "حارس مرمى, تصديات حاسمة", avatarType = "asad_aali",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 29, name = "يوسف", nickname = "الموهوب", position = "Forward",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "مهاجم شاب, تسديدات دقيقة", avatarType = "treka",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 30, name = "محمود", nickname = "الجنرال", position = "Defender",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "قائد خط الدفاع, تنظيم اللعب", avatarType = "ostora",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 31, name = "مصطفى كوارشي", nickname = "الصخرة", position = "Midfielder",
                    level = "Level 2", favoriteClub = "الزمالك",
                    topTraits = "لاعب وسط صخرة, قطع كرات, لياقة عريضة", avatarType = "maldini",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                ),
                PlayerEntity(
                    id = 32, name = "أحمد عبد الخالق", nickname = "المجرم", position = "Forward",
                    level = "Level 2", favoriteClub = "الأهلي",
                    topTraits = "مهاجم مجرم أمام المرمى, تسديدات قوية, هداف", avatarType = "ameed",
                    matchesPlayed = 0, goals = 0, assists = 0, wins = 0, losses = 0, draws = 0, totalPoints = 0, cleanSheets = 0, seasonRating = 6.0f, isAttending = true
                )
            )

            dao.insertPlayers(defaultPlayers)

            // Create initial upcoming Friday match
            val initialMatch = MatchEntity(
                id = 1,
                dateString = com.example.util.MatchDateUtils.getNextFridayDateString(),
                timeString = com.example.util.MatchDateUtils.getDefaultTimeString(),
                targetPlayerCount = 16,
                team1ColorName = "الأبيض",
                team1HexColor = 0xFFFFFFFF,
                team2ColorName = "الأسود",
                team2HexColor = 0xFF1E1E24,
                isFinished = false,
                matchTimestamp = com.example.util.MatchDateUtils.getNextFridayEpochMillis()
            )
            dao.insertMatch(initialMatch)
        }
    }
}
