require("./loader")
// require("./_module")
let n=window.f
let s = n("mvHQ")
    , i = n.n(s)
    , a = n("pFYg")
    , o = n.n(a)
    , r = n("fZjL")
    , l = n.n(r)
let g = n("E7n5")
    , A = n.n(g)
// let A = window.f.n(g)
console.log(A)

let t={kpId: '383a384fabdb4b6aa957689e874006eb'}
function generateHmacSHA256Sign(t) {
    let arguments=[t];
    var e = arguments.length > 1 && void 0 !== arguments[1] ? arguments[1] : "2fa7a73c-66d4-11f0-8925-fa163e54f941"
        , n = l()(t).filter(function(e) {
        return "" !== t[e] && "sign" !== e
    }).sort().map(function(e) {
        var n = t[e];
        return e + "=" + ("object" === (void 0 === n ? "undefined" : o()(n)) && null !== n ? i()(n) : n)
    }).join("&").toLowerCase()
        , s = A.a.HmacSHA256(n, e);
    return A.a.enc.Hex.stringify(s)
}
// 19e7669e20807330d0fc2910a1b769ec3fa983c75467dee9363e3accc3d39a9d
console.log(generateHmacSHA256Sign(t))