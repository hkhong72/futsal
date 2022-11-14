$('#fieldreview').on('click', function () {
    $('.reviewmodal').fadeIn()
})
$('#backtofield').on('click', function () {
    $('.reviewmodal').fadeOut()
})
let memberNickName = document.getElementById("memberNickName").value;
let reviewbutton = document.getElementById("reviewbutton")
let writerFieldName = document.getElementById('writerFieldName').value;
let writerNickName = document.getElementById('writerNickName').value;
let writerReview = document.getElementById('writerReview');
let contextPath = document.getElementById('contextPathHolder').getAttribute('data-contextPath');


reviewbutton.addEventListener('click', review)

async function review() {
    console.log("닉네임 : " + memberNickName);
    console.log("구장명 : " + writerFieldName);
    console.log("작성자 : " + writerNickName);
    console.log("리뷰내용 : " + writerReview.value);
    let targetUrl;

    let area = document.getElementById("reviewInfo");
    if (contextPath != null){
        targetUrl = contextPath + "/field/insertReview?field="+writerFieldName+"&name="+writerNickName+"&review="+writerReview.value
    } else {
        targetUrl = "/field/insertReview?field="+writerFieldName+"&name="+writerNickName+"&review="+writerReview.value
    }
    while(area.hasChildNodes()){
        area.removeChild(area.firstChild);
    }
    let option = {
        method: "post"
    }

    try{
        let res = await fetch(targetUrl, option);
        let result = await res.json();
        console.log("결과 : " + result[0].nickName); // 닉네임 제대로나옴
        //테이블시작
        let tr1 = document.createElement('tr')
        let th1 = document.createElement('th')
        th1.innerText = "구장명"
        let th2 = document.createElement('th')
        th2.innerText = "작성자"
        let th3 = document.createElement('th')
        th3.innerText = "후기"
        let th4 = document.createElement('th')
        th4.innerText = "작성일자"
        let tbody = document.createElement('tbody')
        for(let i=0; i<result.length; i++){
            let tr2 = document.createElement('tr')
            let td1 = document.createElement('td')
            td1.innerText = writerFieldName
            let td2 = document.createElement('td')
            td2.innerText = result[i].nickName
            let td3 = document.createElement('td')
            td3.innerText = result[i].review
            let td4 = document.createElement('td')
            td4.innerText = result[i].regDate
            if(result[i].nickName == memberNickName){
                let td5 = document.createElement('td')
                let td6 = document.createElement('td')
                let btn1 = document.createElement('button')
                btn1.type = "button"
                btn1.id = "modifyReview"
                btn1.value = result[i].review
                btn1.innerText = "수정"

                btn1.addEventListener('click', function modifyReview(e){
                    console.log("해당리뷰작성자 : " + writerNickName3)
                    console.log("해당리뷰내용 : " + e.currentTarget.value)
                    let bfmodifyreview = e.currentTarget.value
                    let area = document.getElementById("reviewInfo");
                    let area2 = document.getElementById("fieldmodifysubmitfrm");
                    while(area.hasChildNodes()){
                        area.removeChild(area.firstChild);
                    }
                    area2.style = "display:none"
                    try{
                        //테이블시작
                        let tr1 = document.createElement('tr')
                        let th1 = document.createElement('th')
                        th1.innerText = "구장명"
                        let th2 = document.createElement('th')
                        th2.innerText = "작성자"
                        let th3 = document.createElement('th')
                        th3.innerText = "후기"

                        let tbody = document.createElement('tbody')
                        let tr2 = document.createElement('tr')
                        let td1 = document.createElement('td')
                        let input1 = document.createElement('input')
                        input1.type = "text"
                        input1.innerText = writerFieldName3
                        input1.readOnly = true
                        input1.name="field"
                        input1.value = writerFieldName3
                        let td2 = document.createElement('td')
                        let input2 = document.createElement('input')
                        input2.type = "text"
                        input2.innerText = writerNickName3
                        input2.readOnly = true
                        input2.name = "name"
                        input2.value = writerNickName3
                        let td3 = document.createElement('td')
                        let textarea = document.createElement('textarea')
                        textarea.name = "writerReview"
                        textarea.id = "modifiedReview"
                        textarea.placeholder = bfmodifyreview

                        let button1 = document.createElement('button')
                        button1.type = "button"
                        button1.name = "reviewbutton"
                        button1.id = "reviewbutton"
                        button1.innerText = "리뷰수정"
                        button1.value = bfmodifyreview
                        button1.addEventListener('click', modify)

                        td1.append(input1)
                        td2.append(input2)
                        td3.append(textarea)
                        tr2.append(td1)
                        tr2.append(td2)
                        tr2.append(td3)
                        tbody.append(tr2)
                        tr1.append(th1)
                        tr1.append(th2)
                        tr1.append(th3)
                        area.append(tr1)
                        area.append(tbody)
                        area.append(button1)

                    }catch(err){
                        alert("수정버튼눌렀을때 err : " + err);
                    }

                })

                let btn2 = document.createElement('button')
                btn2.type = "button"
                btn2.id = "deleteReview"
                btn2.value = result[i].regDate
                btn2.innerText = "삭제"
                btn2.addEventListener('click', remove)

                td5.append(btn1)
                td6.append(btn2)
                tr2.append(td1)
                tr2.append(td2)
                tr2.append(td3)
                tr2.append(td4)
                tr2.append(td5)
                tr2.append(td6)
                tbody.append(tr2)
            }else{
                tr2.append(td1)
                tr2.append(td2)
                tr2.append(td3)
                tr2.append(td4)
                tbody.append(tr2)
            }
        }
        tr1.append(th1)
        tr1.append(th2)
        tr1.append(th3)
        tr1.append(th4)
        area.append(tr1)
        area.append(tbody)
        $('#writerReview').val('');
    }catch(err){
        alert(err);
    }

}



