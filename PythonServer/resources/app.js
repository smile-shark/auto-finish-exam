
require("./resources/loader")
// require("./loader")

let n=window.f
let s = n("mvHQ")
    , i = n.n(s)
    , a = n("pFYg")
    , o = n.n(a)
    , r = n("fZjL")
    , l = n.n(r)
let g = n("E7n5")
    , A = n.n(g)

function generateHmacSHA256Sign(t) {
    try{
        let res=JSON.parse(t)
        t=res
    }catch(e){
        // 报错就说明传递的不是json，直接使用
    }
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
// kpid加密
// 19e7669e20807330d0fc2910a1b769ec3fa983c75467dee9363e3accc3d39a9d
// let t={kpId: '383a384fabdb4b6aa957689e874006eb'}
// console.log(generateHmacSHA256Sign(t))


// 回答结果两次加密
// let t={
//     "kpid": "1dfaecf617064cb9bbadf0393be17cbd",
//     "questions": [],
//     "sign": ""
// }
// console.log(generateHmacSHA256Sign(t))
// let t2={
//     "kpid": "1dfaecf617064cb9bbadf0393be17cbd",
//     "questions": [
//         {
//             "QuestionID": "4b6088bc258d45328b7b8cc7c97a19ee",
//             "AnswerID": "d0993fd488724dd5b5f45725b670df70"
//         }
//     ],
//     "sign": "68fa68f168704500e37d89927d358bc7fcb8a0be7c90553b04000bc009e4e503"
// }
// console.log(generateHmacSHA256Sign(t2))