package com.aladdin.dailyplate.data.local

import kotlinx.serialization.json.Json

/** Explicit nulls and defaults are part of payload schema 1. Reject corrupt/unknown fields. */
internal val snapshotJson = Json { encodeDefaults = true; explicitNulls = true }
