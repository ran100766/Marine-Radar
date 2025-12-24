package com.example.gps_compas

fun getInitialReferencePoints(): MutableList<ReferencePoint> {
    return mutableListOf(
        ReferencePoint("Marina_Nahariya", 33.012278, 35.089028),
        ReferencePoint("Akko_Marina", 32.919944, 35.067778),
        ReferencePoint("Haifa_Marina", 32.815639, 35.028361),
        ReferencePoint("Marina_Herzliya", 32.165806, 34.792583),
        ReferencePoint("Marina_Tel Aviv", 32.087778, 34.766667),
        ReferencePoint("Jaffa_Port", 32.053083, 34.750222),
        ReferencePoint("Marina_Ashdod", 31.826250, 34.637528),
        ReferencePoint("Marina_Ashkelon", 31.682250, 34.555833),
        ReferencePoint("Eilat_Marina", 29.545167, 34.960019),

        // CYPRUS – Green entrance lights
        ReferencePoint("Limassol_Port", 34.6716, 33.0419),
        ReferencePoint("Larnaca_Port", 34.9116, 33.6473),
        ReferencePoint("Paphos_Port", 34.7579, 32.4088),
        ReferencePoint("Ayia_Napa_Marina", 34.9824, 34.0019),
        ReferencePoint("Zygi_Marina", 34.7337, 33.3331),

        // GREECE – Mainland
        ReferencePoint("Piraeus_Port", 37.9356, 23.6284),
        ReferencePoint("Rafina_Port", 38.0219, 24.0067),
        ReferencePoint("Lavrio_Port", 37.7144, 24.0564),
        ReferencePoint("Thessaloniki_Port", 40.6276, 22.9335),
        ReferencePoint("Volos_Port", 39.3610, 22.9436),

        // CRETE
        ReferencePoint("Heraklion_Port", 35.3443, 25.1360),
        ReferencePoint("Chania_Port", 35.5197, 24.0148),
        ReferencePoint("Rethymno_Port", 35.3729, 24.4730),
        ReferencePoint("Agios_Nikolaos_Marina", 35.1909, 25.7152),

        // IONIAN SEA
        ReferencePoint("Corfu_Port", 39.6200, 19.9190),
        ReferencePoint("Gouvia_Marina", 39.6524, 19.8486),
        ReferencePoint("Lefkada_Marina", 38.8304, 20.7096),
        ReferencePoint("Preveza_Marina", 38.9594, 20.7522),
        ReferencePoint("Zakynthos_Port", 37.7809, 20.8957),


// AEGEAN SEA
        ReferencePoint("Mykonos_Port", 37.4466, 25.3289),
        ReferencePoint("Paros_Parikia_Port", 37.0856, 25.1475),
        ReferencePoint("Naxos_Port", 37.1064, 25.3726),
        ReferencePoint("Rhodes_Mandrakia", 36.4515, 28.2276),
        ReferencePoint("Kos_Marina", 36.8897, 27.3036)
    )
}
