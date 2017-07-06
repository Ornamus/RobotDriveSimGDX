package ryan.game.competition;

import ryan.game.Utils;

public class NameMagic {
    
    public static String generateName() {
        String one = array1[Utils.randomInt(0, array1.length-1)];
        String two = array2[Utils.randomInt(0, array2.length-1)];
        String three = array3[Utils.randomInt(0, array3.length-1)];
        if (Utils.randomInt(1, 2) != 1) one = "";
        if (Utils.randomInt(1, 4) == 1) two = "";
        if (!one.equals("") || !two.equals("")) {
            if (Utils.randomInt(1, 10) == 1) {
                three = "";
                //Utils.log("VVVV created a special VVV");
            }
        }
        String name = one + " " + two + " " + three;
        while (name.contains("  ")) name = name.replaceAll("  ", " ");
        while (name.startsWith(" ")) name = name.replaceFirst(" ", "");
        while (name.endsWith(" ")) name = name.substring(0, name.length() - 1);
        return name;
    }

    //TODO: implement "The" instead of a random amount of "The" being in this array
    static String[] array1 = {"Ancient","Angry","Annoying","Artificial","Atomic","Awful","Bad","Beige","Big","Bitter","Black","Blue","Boiling",
           "Bottom","Bouncing","Brass","Breakaway","Brown","Bumbling","Buzzing","Bygone","Cheesy","Chillin","Choking","Chronic","Clumsy","Cold",
           "Copperhead","Country","Curving","Cutting","Damaged","Dancing","Dastardly","Dead","Deadly","Deep","Dirty","Distinguished","Dramatic",
           "Drinking","East","Eastern","El","Electric","Emotional","End","Engorged","Enraged","Exploding","Extended","Famous","Fat","Final","Finishing",
           "Flashing","Flattened","Flaunting","Flying","Former","Fortunate","Foul","Friday","Frozen","Fumbling","Furious","Gentle","Giant",
           "Gigantic","Gloating","Global","Gold","Golden","Gonzo","Good","Gothic","Great","Green","Gritty","Grumbling","Grumpy","Grunting","Hack",
           "Hard","Headless","Heartland","Hideous","Hilltop","Hissing","Homeland","Hopping","Hot","Huge","Humble","Indigo",
           "Infamous","Inland","Inner","Instant","International","Invisible","Irrational","Irregular","Irrelevant","Johnny","Jumping","Junk",
           "Killing","La","Lady","Lake","Large","Las","Latin","Lazy","Le","Leaping","Legal","Legendary","Limping","Lingering","Little","Long",
           "Longshot","Los","Lucky","Macho","Magenta","Majestic","Marvelous","Mauve","Mental","Mighty","Moist","Multiple","Mysterious","Nada","New",
           "Nile","No","Noble","North","Northern","Nutty","Optimistic","Orange","Outer","Paper","Part-Time","Pastel","Peculiar","Pesky",
           "Plastic","Prancing","Prickly","Prison","Psychic","Psychotic","Purple","Pyroclastic","Quivering","Rabid","Random","Ravaged","Ravishing",
           "Raw","Reconditioned","Red","Redundant","Reformed","Regular","Roasting", "Robotic","Royal","Running","Salty","Salty","Saturday","Savage","Scalding",
           "Screaming","Sealevel","Seaside","Seaview","Shivering","Shooting","Short","Silver","Simmering","Sitting","Skinny","Small",
           "Soaring","Soft","South","Southern","Spiked","Spinning","Sprinting","Standing","Stealth","Steel","Stiff","Stomping","Strategic",
           "Streaking","Streetwise","Strutting","Stumbling","Stupid","Sucky","Sunday","Sweaty","Sweeping","Sweet","Swishing","Tacky","Tainted",
           "Tall","Tan","Terrible","Terrifying","Testy","The","The","The","The","The","The","The","The","The","The","The","The","The","The",
           "The","The","The","The","The","The","The","The","The","The","The","The","The","The","The","The","Thick","Tiny","Titanium","Top",
           "Tri-City","Tri-County","Triple","Tripping","Trudging","Twirling","Ugly","Unemployed","Unfair","Vaulting","Victorious","Violet","Viral",
           "Volleywood","Walking","Watery","Wayside","West","Western","Whirling","White","Wise","Woebegone","Yellow"};

    static String[] array2 = {"Air","Astro",
           "Axe","Bacon","Ball","Ballz","Bayside","Bean","Beat","Bedtime","Beige","Black","Blank","Blue","Boomerang","Bovine","Breakfast","Brick",
           "Brown","Bug","Bush","Butt","Butte","Cat","Caviar","Champagne","Chemical","City","Cliff","Cloud","Concrete","Court","Day","Death",
           "Desert","Diamond","Dog","Donkey","Dope","Drug","End","Endzone","Field","Fire","Foam","Free","Freedom", "Frog", "Geo","Goal","Gonzo","Goon",
           "Grass","Gravity","Green","Grunting","Happy","Hill","Indigo","InnerCity","Jet","Karma","Land","Laughing","Leather",
           "Liberty","Lightning","Luck","Lucky","Lunatic","Mad","Magenta","Magic","Marsh","Mauve","Metal","Midnight","Milk","Mountain","Murder",
           "Net","Night","Nova","Novelty","Ocean","Orange","Ozone","Paper","Pastel","Picnic", "Pink", "Pinewoods","Pony","Postal","Puck","Pulp","Purple",
           "Rag","Red","Return","Roast", "Robot","Score","Scoreless","Sea","Sky","Slime","Smoke","Sore","Space","Steel","Streak","Street","Strike",
           "Stucco","Surf","Swamp","Sweat","Tan","Taupe","Thunder","Titanium","Touch","Tundra","Turf","Turtle","Uniform","Violet","Volley",
           "Voodoo","Water","White","Whiz","Wicket","Wood","World","Worm","Yellow","Zone"};

    static String[] array3 = {"Addicts","Afrostars","Aftermath","Aggies","Anacondas","Apocalypse","Armageddon","Assassins",
           "Attackers","Avalanche","Avengers","Babies","Bangers","Banshees","Basilisks","Beancounters","Beaters","Beatniks","Bison","Blast",
           "Blasters","Blisters","Blitz","Bluehairs","Boils","Bombers","Boneheads","Boxers","Boys","Bricks","Bruisers","Bugs","Burnouts","Busters","Butterflies",
           "Buzzards","Cacophony","Carp","Chameleons","Champs","Chard","Chillers","Chokers","Chumps","Cicadas","Cleavage","Clones","Clowns","Cockpits",
           "Cockroaches","Corsairs","Crabs","Crawlers","Crew","Croppers","Crows","Crunchers","Cult","Curves","Cyclops","Dabblers","Damage","Deadheads","Dervish",
           "Desperados","Destroyers","Diggers","Dirtbags","Doctors","Dogs","Dribblers","Ducks","Dudes","Dunkers","Dwellers","Easterners","Einsteins","Elephants",
           "Elves","Empire","Enchiladas","Epidemic","Fairies","Fallout","Family","Farmers","Feeders","Femmes","Fighters","Finish","Fish","Flash","Flashers",
           "Flies","Flotsam","Flu","Flux","Flyers","Force","Frogs","Gaffers","Gals","Gamblers","Gamers","Gang","Gargoyles","Gators","Gazelles","Generals",
           "Gentlemen","Geriatrics","Giants","Girls","Gnomes","Goats","Godzillas","Gold","Gophers","Grasshoppers","Graybeards","Greenbacks","Greenspans",
           "Grinders","Groundhogs","Grumblers","Gunners","Gunslingers","Guppies","Guys","Hackers","Hardbodies","Hardhats","Hatters","Hillbillies","Hitmen",
           "Hollyrollers","Honkers","Hookers","Hoops","Hoppers","Horsemen","Hostages","Hotshots","Humblers","Hurl","Hurricanes","Hyenas","Icebergs",
           "Imperials","Jackals","Junkers","Killers","Kings","Kittens","Landslide","Leaders","Leprechauns","Llamas","Logic","Longhairs","Longshots","Losers",
           "Lovers","Machine","Magicians","Mailers","Makers","Maniacs","Marionettes","Maulers","Maxwells","Meteors","Militia","Milkers","Miltons","Mimes",
           "Mirage","Missiles","Mobsters","Monkeys","Monsters","Mosquitoes","Mounders","Muzak","Nailers","Nazis","Nerds","Newts","Nitpickers","Nodes","Noose",
           "Norsemen","Northerners","Oldtimers","Operatives","Opossums","Oracles","Order","Orphans","Outlaws","OwnGoalers","Oxen","Pandas","Pandemic",
           "Pandoras","Patsies","Penises","Pests","Phobias","Piranhas","Pirates","Pixies","Plainsmen","Planets","Plasma","Players","Pod","Porcupines","Prawns",
           "Predators","Presidents","Prisoners","Professors","Puppets","Puppies","Pussies","Queens","Racers","Ramblers","Rappers","Rats","Razors","Rebels",
           "Rebuttals","Redheads","Renegade","Reptiles","Resistance","Rex","Riot","Rockers","Rockets", "Robots","Rollers","Roosters","Rounders","Runners","Rush",
           "Salamanders","Samaritans","Samurai","Savages","Scorers","Scumbags","Sensation","Sentinels","Sharks","Silencers","Silos","Silverbacks",
           "Silverfish","Simpletons","Skeletons","Skyhooks","Slammers","Sliders","Sluts","Snakes","Southerners","Spawn","Spikers","Spinners","Spores","Spree",
           "Sprinters","Spuds","Squad","Squall","Squares","Squirrels","Stampede","Steers","Stingers","Stompers","Stoners","Storm","Stormers","Stormtroopers",
           "Streakers","Strikers","Stripes","Strokers","Stumblers","Summit","Supernovas","Surgeons","Swells","Syndrome","Talcum","Tamales","Tarts","Termites",
           "Terror","Threat","Thrillers","Thunderballs","Tigresses","Tigrettes","Titans","Toads","Toddlers","Tornadoes","Tossers","Tractors","Traders","Trample",
           "Tramps","Triangles","Trippers","Troopers","Turmoil","Turtles","Twins","Twisters","Universe","Urge","Vampires","Vanguard","Vegetables","Venison",
           "Vigilantes","Vikings","Wallabies","Wankers","Wannabees","Warthogs","Wasps","Wedge","Werewolves","Westerners","Whackers","Wheelers","Wigglers",
           "Wipeout","Wolfpack","Woodies","Worms","Wranglers","Zealots","Zombies","Zone","Zonkers"};

}