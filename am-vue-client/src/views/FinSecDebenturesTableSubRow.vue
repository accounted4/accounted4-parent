<style scoped>
    .expand-row{
        margin-bottom: 16px;
    }
</style>


<template>

    <div>

        <Row class="expand-row">
            <Col span="8">
                <span class="expand-key">Rate: </span>
                <span class="expand-value">{{ row.percentage }}%</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Issue date: </span>
                <span class="expand-value">{{ row.issueDte }}</span>
            </Col>
        </Row>

        <Row class="expand-row">
            <Col span="8">
                <span class="expand-key">Underlying: </span>
                <span class="expand-value">{{ row.underlyingSymbol }}</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Close: </span>
                <span class="expand-value">{{ row.underlyingClosePrice }}</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Read Date: </span>
                <span class="expand-value">{{ row.underlyingReadDte }}</span>
            </Col>
        </Row>

        <Row class="expand-row">
            <Col span="8">
                <span class="expand-key">Conversion Price: </span>
                <span class="expand-value">${{ row.conversionPrice }}</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Conversion Rate: </span>
                <span class="expand-value">{{ row.conversionRate }}</span>
            </Col>
            <Col span="8">
                <span class="expand-key">Converted: </span>
                <span class="expand-value">${{ row.converted }}</span>
            </Col>
        </Row>

        <Row class="expand-row">
            <Col>
                <span class="expand-key">Prospectus: </span>
                <a v-bind:href="row.prospectus" target="_blank">{{ row.prospectus }}</a>
            </Col>
        </Row>

        <Row class="expand-row">
            <Col>
                <p>{{ row.comments }}</p>
            </Col>
        </Row>

        <Row>
            <Col span="8">
                <sparkline>
                    <sparklineLine :data="closePrice" :limit="closePrice.length" :styles="spLineStyles1" />
                </sparkline>
            </Col>

            <Col span="8">
                <sparkline>
                    <sparklineBar
                            :data="volumeTraded"
                            :margin="spMargin4"
                            :limit="volumeTraded.length"
                            :styles="volumeBarStyle"
                            :refLineStyles="spRefLineStyles4" />
                </sparkline>
            </Col>

        </Row>

    </div>

</template>


<script>

    import {getQuoteHistory} from "@/js/finSecService";
    // http://www.7te.net/zp-ui/#/sparkline


    export default {

        props: {
            row: Object
        },

        data() {
            return {
                closePrice: [],
                spIndicatorStyles1: false,
                spLineStyles1: {
                    stroke: '#54a5ff'
                },

                volumeTraded: [],
                spMargin4: 2,
                volumeBarStyle: {
                    fill: '#54a5ff'
                },
                spLineStyles4: {
                    stroke: '#d14'
                },
                spRefLineStyles4: {
                    stroke: '#d14',
                    strokeOpacity: 1,
                    strokeDasharray: '3, 3'
                }

            }
        },

        mounted: function() {
            this.getQuoteHistory(this.row.instrumentId);
        },


        methods: {

            getQuoteHistory: function (instrumentId) {
                getQuoteHistory(instrumentId).then(restResponse => {
                    this.loading = true;
                    this.closePrice = restResponse.map(q => q.closePrice);
                    this.volumeTraded = restResponse.map(q => q.volumeTraded);
                    this.loading = false;
                });

            }

        }

    };

</script>