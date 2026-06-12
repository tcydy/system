<script setup>
import { ref } from 'vue'
import { Shop } from '@element-plus/icons-vue'

const sideBar = ref('我买到的')

</script>

<template>
    <div style="width:100%;height:100vh;background-color: #f1f1f1;">
        <div style="width:80%;margin:0 auto;display: flex;gap:50px;">

            <div style="width:300px;">
                <el-card style="border-radius: 10px">
                    <div class="left-card-header">
                        <el-icon><Shop /></el-icon>
                        <h3>我的交易</h3>
                    </div>
                    
                    <div class="button-buy-sale":class="{'active': sideBar==='我买到的'}" @click="sideBar='我买到的'">
                        我买到的
                    </div>

                    <div class="button-buy-sale":class="{'active': sideBar==='我卖出的'}" @click="sideBar='我卖出的'">
                        我卖出的
                    </div>

                    <div>
                        
                    </div>

                </el-card>
            </div>

            <div style="flex:1;display: flex;flex-direction: column;gap:10px;">
                <el-card style="border-radius: 10px">

                </el-card>
                <el-card style="border-radius: 10px">

                </el-card>
                <div>
                    <el-card style="border-radius: 10px">

                    </el-card>
                </div>
                <el-card style="border-radius: 10px">

                </el-card>

            </div>

        </div>
    </div>



</template>

<style scoped>
.active{
    background-color: #f1f1f1;
}
.button-buy-sale{
    width:100%;
    height:50px;
    display:flex;
    align-items: center;
    padding:10px;
    border-radius: 10px;
}
.left-card-header{
    display:flex;
    align-items: center;
    gap:10px;
    .el-icon{
        font-size: 22px;
    }
}
</style>