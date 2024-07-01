package com.example.run_core.data.local

data class DirectionsResponse(val routes: List<Route>)
data class Route(val overviewPolyline: OverviewPolyline)
data class OverviewPolyline(val points: String)