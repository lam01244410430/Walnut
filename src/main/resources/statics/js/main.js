/**
 * main.js - Xử lý logic chung cho toàn bộ website
 */

// 1. Xác nhận khi người dùng bấm nút Đăng xuất
function confirmLogout() {
    return confirm("您确定要退出系统吗？");
}

// 2. Demo: Hiển thị thông báo khi gửi câu hỏi cho chuyên gia
function submitQuestion() {
    const question = document.querySelector('textarea').value;
    if (question.trim() === "") {
        alert("请输入您的问题！");
        return;
    }
    // Giả lập gửi thành công
    alert("✅ 提交成功！专家将在24小时内回复您。");

    // Xóa nội dung ô nhập
    document.querySelector('textarea').value = "";
}

// 3. Demo: Hiển thị thông báo khi Admin upload tài liệu
function submitUpload() {
    alert("✅ 资料上传成功！");
    // Chuyển hướng về trang chủ (Demo)
    window.location.href = "/";
}

// 4. In báo cáo (Dùng ở trang Dự báo sản lượng)
function printReport() {
    window.print();
}